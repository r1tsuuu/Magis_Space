(function () {
'use strict';
var API = /^https?:$/.test(location.protocol) ? location.origin + '/api' : 'http://localhost:9999/api';
var STATUS_LABEL = { VACANT: 'Vacant', OCCUPIED: 'Occupied', UNVERIFIED: 'Unverified', NO_DATA: 'No data' };
var state = { buildings: [], amenities: [], students: [], studentId: null, view: { building: null, floor: null },
  mapRooms: [], notifs: [], seenNotifs: null, subs: [], lastOk: null };
var $ = function (id) { return document.getElementById(id); };

function el(tag, cls, text) {
  var e = document.createElement(tag);
  if (cls) e.className = cls;
  if (text != null) e.textContent = text;
  return e;
}
function clear(n) { while (n.firstChild) n.removeChild(n.firstChild); }

function api(method, path, body) {
  var opt = { method: method, headers: { 'Accept': 'application/json' } };
  if (body !== undefined) { opt.headers['Content-Type'] = 'application/json'; opt.body = JSON.stringify(body); }
  return fetch(API + path, opt).then(function (r) {
    return r.text().then(function (t) {
      var data = null;
      try { data = t ? JSON.parse(t) : null; } catch (e) { data = t; }
      if (!r.ok) {
        var err = new Error((data && data.message) || (typeof data === 'string' && data) || ('Request failed (' + r.status + ')'));
        err.status = r.status; throw err;
      }
      return data;
    });
  }, function () { throw new Error('Cannot reach the Magis Space server at ' + API); });
}

function toast(msg, isErr) {
  var t = el('div', 'toast' + (isErr ? ' err' : ''), msg);
  $('toasts').appendChild(t);
  setTimeout(function () { if (t.parentNode) t.parentNode.removeChild(t); }, isErr ? 7000 : 4500);
}
function fail(e) { toast(e.message || String(e), true); }

function ago(min) {
  if (min == null) return null;
  if (min < 1) return 'just now';
  if (min < 60) return min + ' min ago';
  var h = Math.floor(min / 60);
  return h + (h === 1 ? ' hour ago' : ' hours ago');
}
function fmtTime(s) {
  var d = new Date(s);
  if (isNaN(d)) return s;
  return d.toLocaleString([], { month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit' });
}
function likelyFree(r) { return r.predictedVacant && (r.status === 'NO_DATA' || r.status === 'UNVERIFIED'); }

function loadStudents() {
  return api('GET', '/students').then(function (list) {
    state.students = list;
    var saved = parseInt(localStorage.getItem('magis.studentId'), 10);
    var ok = list.some(function (s) { return s.id === saved; });
    state.studentId = ok ? saved : null;
    var sel = $('studentSelect');
    clear(sel);
    var o = el('option', '', 'Choose your name…'); o.value = ''; sel.appendChild(o);
    list.forEach(function (s) { var op = el('option', '', s.name); op.value = s.id; sel.appendChild(op); });
    sel.value = state.studentId || '';
  });
}
function setStudent(id) {
  state.studentId = id || null;
  if (id) localStorage.setItem('magis.studentId', id); else localStorage.removeItem('magis.studentId');
  state.seenNotifs = null; state.notifs = [];
  return loadPersonal(true);
}
$('studentSelect').addEventListener('change', function () { setStudent(parseInt(this.value, 10) || null); });

var dlg = $('studentDialog');
$('newStudentBtn').addEventListener('click', function () {
  if (dlg.showModal) dlg.showModal(); else dlg.setAttribute('open', '');
  $('nsName').focus();
});
$('nsCancel').addEventListener('click', function () { dlg.close(); });
$('studentForm').addEventListener('submit', function (ev) {
  ev.preventDefault();
  api('POST', '/students', { name: $('nsName').value.trim(), email: $('nsEmail').value.trim(), phone: $('nsPhone').value.trim() })
    .then(function (s) {
      dlg.close();
      $('studentForm').reset();
      return loadStudents().then(function () { $('studentSelect').value = s.id; return setStudent(s.id); })
        .then(function () { toast('Welcome, ' + s.name + '!'); });
    }).catch(fail);
});

function loadBuildings() {
  return api('GET', '/map/buildings').then(function (b) {
    state.buildings = b;
    ['fBuilding', 'sBuilding', 'fFrom'].forEach(function (id) {
      var sel = $(id), cur = sel.value, first = sel.options[0];
      clear(sel);
      if (first) sel.appendChild(first);
      b.forEach(function (x) { var o = el('option', '', x.building); o.value = x.building; sel.appendChild(o); });
      if (cur && b.some(function (x) { return x.building === cur; })) sel.value = cur;
    });
  });
}
function loadAmenities() {
  return api('GET', '/amenities').then(function (a) {
    state.amenities = a;
    ['fAmenity', 'sAmenity'].forEach(function (id) {
      var sel = $(id), cur = sel.value, first = sel.options[0];
      clear(sel); sel.appendChild(first);
      a.forEach(function (x) { var o = el('option', '', x.name); o.value = id === 'fAmenity' ? x.name : x.id; sel.appendChild(o); });
      sel.value = cur;
    });
  });
}

function renderCrumbs() {
  var c = $('crumbs'); clear(c);
  if (!state.view.building) { c.textContent = 'Ateneo campus · select a building'; return; }
  var a = el('a', '', 'Campus'); a.addEventListener('click', function () { showCampus(); });
  c.appendChild(a); c.appendChild(document.createTextNode(' / ' + state.view.building));
}
function showCampus() { state.view.building = null; state.view.floor = null; refreshMap(); }
function showBuilding(name) { state.view.building = name; state.view.floor = null; refreshMap(); }

function refreshMap() {
  renderCrumbs();
  if (!state.view.building) { $('mapTitle').textContent = 'Campus'; $('floors').hidden = true; return loadBuildings().then(drawCampus); }
  $('mapTitle').textContent = state.view.building;
  return api('GET', '/map?building=' + encodeURIComponent(state.view.building)).then(function (rooms) {
    state.mapRooms = rooms; drawFloor();
  }, function (e) {
    if (e && e.status === 404) { state.view.building = null; state.view.floor = null; return refreshMap(); }
    throw e;
  });
}

function drawCampus() {
  var map = $('map'); clear(map); map.className = 'map campus';
  var b = state.buildings;
  if (!b.length) { map.appendChild(el('div', 'skeleton', 'No buildings yet.')); return; }
  var placed = b.filter(function (x) { return x.latitude != null && x.longitude != null; });
  var loose = b.filter(function (x) { return x.latitude == null || x.longitude == null; });
  function makePin(x) {
    var p = el('button', 'pin' + (x.vacantCount > 0 ? ' has' : ''));
    p.type = 'button';
    p.title = x.building + ': ' + x.vacantCount + ' of ' + x.totalRooms + ' rooms vacant';
    var bub = el('div', 'bubble');
    bub.appendChild(el('div', 'bn', x.building));
    var bc = el('div', 'bc'); bc.appendChild(el('i')); bc.appendChild(el('span', '', x.vacantCount + ' / ' + x.totalRooms + ' vacant'));
    bub.appendChild(bc); p.appendChild(bub); p.appendChild(el('div', 'tip'));
    p.addEventListener('click', function () { showBuilding(x.building); });
    return p;
  }
  var canvas = el('div', 'canvas'); map.appendChild(canvas);
  var pins = placed.map(function (x) { var p = makePin(x); canvas.appendChild(p); return { b: x, el: p }; });
  if (loose.length) {
    var foot = el('div', 'unplaced');
    foot.appendChild(el('span', 'ul', 'No coordinates yet:'));
    loose.forEach(function (x) { var p = makePin(x); p.classList.add('static'); foot.appendChild(p); });
    map.appendChild(foot);
  }
  if (!pins.length) { canvas.style.height = '0'; return; }
  layoutPins(canvas, pins);
  var n = el('div', 'north', 'N'); canvas.appendChild(n);
  map.appendChild(el('div', 'mapnote', 'Schematic map: buildings keep their north-south and east-west order. Pins are nudged apart when close together.'));
}
function layoutPins(canvas, pins) {
  var W = canvas.clientWidth || 600;
  var H = W < 640 ? 440 : Math.max(400, Math.min(560, Math.round(W * 0.46)));
  canvas.style.height = H + 'px';
  var GAP = 8, EDGE = 8;
  pins.forEach(function (p) { p.w = p.el.offsetWidth; p.h = p.el.offsetHeight; });
  var las = pins.map(function (p) { return p.b.latitude; }), los = pins.map(function (p) { return p.b.longitude; });
  var minLa = Math.min.apply(null, las), maxLa = Math.max.apply(null, las), minLo = Math.min.apply(null, los), maxLo = Math.max.apply(null, los);
  var maxW = Math.max.apply(null, pins.map(function (p) { return p.w; })), maxH = Math.max.apply(null, pins.map(function (p) { return p.h; }));
  var padX = maxW / 2 + EDGE, padT = maxH + EDGE, padB = EDGE + 6;
  var availW = W - 2 * padX, availH = H - padT - padB;
  function frac(vals, v, min, max) {
    var u = vals.filter(function (x, i) { return vals.indexOf(x) === i; }).sort(function (a, b) { return a - b; });
    var lin = max > min ? (v - min) / (max - min) : 0.5, rank = u.length > 1 ? u.indexOf(v) / (u.length - 1) : 0.5;
    return 0.5 * lin + 0.5 * rank;
  }
  pins.forEach(function (p) {
    p.tx = padX + frac(los, p.b.longitude, minLo, maxLo) * availW;
    p.ty = padT + (1 - frac(las, p.b.latitude, minLa, maxLa)) * availH;
    p.cx0 = p.tx; p.cy0 = p.ty - p.h / 2;
  });
  var offsets = [];
  for (var ddx = -W; ddx <= W; ddx += 8) for (var ddy = -H; ddy <= H; ddy += 8) offsets.push([ddx, ddy, ddx * ddx + ddy * ddy]);
  offsets.sort(function (m, n) { return m[2] - n[2]; });
  var ok = true, done = [];
  pins.slice().sort(function (p, q) { return p.ty - q.ty || p.tx - q.tx; }).forEach(function (p) {
    if (!ok) return;
    var found = false;
    for (var k = 0; k < offsets.length && !found; k++) {
      var cx = p.cx0 + offsets[k][0], cy = p.cy0 + offsets[k][1];
      if (cx < p.w / 2 + EDGE || cx > W - p.w / 2 - EDGE || cy < p.h / 2 + EDGE || cy > H - p.h / 2 - EDGE) continue;
      var free = done.every(function (q) { return Math.abs(q.cx - cx) >= (q.w + p.w) / 2 + GAP || Math.abs(q.cy - cy) >= (q.h + p.h) / 2 + GAP; });
      if (free) { p.cx = cx; p.cy = cy; found = true; }
    }
    if (found) done.push(p); else ok = false;
  });
  if (!ok) {
    canvas.parentNode.setAttribute('data-grid', '1');
    var order = pins.slice().sort(function (p, q) { return q.b.latitude - p.b.latitude || p.b.longitude - q.b.longitude; });
    var cols = Math.max(1, Math.floor((W - 2 * EDGE + GAP) / (maxW + GAP)));
    var rows = Math.ceil(order.length / cols);
    var cellW = (W - 2 * EDGE) / cols;
    canvas.style.height = Math.max(rows * (maxH + GAP) + 2 * EDGE + 6, W < 640 ? 0 : 400) + 'px';
    order.forEach(function (p, i) {
      p.cx = EDGE + cellW * (i % cols) + cellW / 2; p.cy = EDGE + Math.floor(i / cols) * (maxH + GAP) + maxH / 2; p.grid = true;
    });
  } else if (W >= 640) {
    var NS = 'http://www.w3.org/2000/svg', svg = document.createElementNS(NS, 'svg');
    svg.setAttribute('class', 'links'); svg.setAttribute('width', W); svg.setAttribute('height', H);
    pins.forEach(function (p) {
      var ex = p.cx, ey = p.cy + p.h / 2, d = Math.sqrt((ex - p.tx) * (ex - p.tx) + (ey - p.ty) * (ey - p.ty));
      if (d > 12) {
        if (d > 50) {
          var l = document.createElementNS(NS, 'line');
          l.setAttribute('x1', p.tx); l.setAttribute('y1', p.ty); l.setAttribute('x2', ex); l.setAttribute('y2', ey);
          svg.appendChild(l);
        }
        var dot = document.createElementNS(NS, 'circle');
        dot.setAttribute('cx', p.tx); dot.setAttribute('cy', p.ty); dot.setAttribute('r', 3.5);
        svg.appendChild(dot);
      }
    });
    canvas.insertBefore(svg, canvas.firstChild);
  }
  pins.forEach(function (p) { p.el.style.left = p.cx + 'px'; p.el.style.top = (p.cy + p.h / 2) + 'px'; });
}
function roomBox(r) {
  var box = el('div', 'room ' + r.status);
  var rn = el('div', 'rn'); rn.appendChild(el('span', '', r.name)); rn.appendChild(el('small', '', 'Floor ' + r.floor)); box.appendChild(rn);
  box.appendChild(el('div', 'st', r.status === 'NO_DATA' ? 'No data' : STATUS_LABEL[r.status].toUpperCase()));
  var a = ago(r.minutesAgo);
  if (a) box.appendChild(el('div', 'meta', 'Reported ' + a));
  if (likelyFree(r)) box.appendChild(el('div', 'hint', 'Likely free (no class now)'));
  box.appendChild(reportButtons(r.roomId != null ? r.roomId : r.id));
  return box;
}
function reportButtons(roomId) {
  var d = el('div', 'report');
  [['v', 'Vacant', false], ['o', 'Occupied', true]].forEach(function (x) {
    var b = el('button', x[0], x[1]); b.type = 'button';
    b.title = 'Report this room as ' + x[1].toLowerCase();
    b.addEventListener('click', function () { report(roomId, x[2], d); });
    d.appendChild(b);
  });
  return d;
}

function drawFloor() {
  var all = state.mapRooms, map = $('map');
  var floors = Array.from(new Set(all.map(function (r) { return r.floor; }))).sort(function (a, b) { return a - b; });
  if (state.view.floor == null || floors.indexOf(state.view.floor) < 0) state.view.floor = floors.length ? floors[0] : null;
  var fl = $('floors'); clear(fl);
  fl.hidden = floors.length < 2;
  floors.forEach(function (f) {
    var c = el('button', 'chip' + (f === state.view.floor ? ' on' : ''), 'Floor ' + f); c.type = 'button';
    c.addEventListener('click', function () { state.view.floor = f; drawFloor(); });
    fl.appendChild(c);
  });
  var rooms = all.filter(function (r) { return r.floor === state.view.floor; })
    .sort(function (a, b) { return a.name.localeCompare(b.name, undefined, { numeric: true }); });
  clear(map); map.className = 'map floor';
  if (!rooms.length) { map.appendChild(el('div', 'skeleton', 'No rooms in this building yet.')); return; }
  rooms.forEach(function (r) { map.appendChild(roomBox(r)); });
}
function loadList() {
  var q = [];
  if ($('fBuilding').value) q.push('building=' + encodeURIComponent($('fBuilding').value));
  var cap = parseInt($('fCap').value, 10);
  if (cap > 0) q.push('minCapacity=' + cap);
  if ($('fAmenity').value) q.push('amenity=' + encodeURIComponent($('fAmenity').value));
  var from = $('fFrom').value;
  var reqs = [api('GET', '/rooms' + (q.length ? '?' + q.join('&') : ''))];
  if (from) reqs.push(api('GET', '/map?fromBuilding=' + encodeURIComponent(from)));
  return Promise.all(reqs).then(function (res) {
    var rooms = res[0], st = $('fStatus').value;
    if (st === 'LIKELY') rooms = rooms.filter(likelyFree);
    else if (st) rooms = rooms.filter(function (r) { return r.status === st; });
    if (res[1]) {
      var order = {}; res[1].forEach(function (m, i) { order[m.roomId] = i; });
      rooms.sort(function (a, b) {
        var va = a.status === 'VACANT' ? 0 : 1, vb = b.status === 'VACANT' ? 0 : 1;
        if (va !== vb) return va - vb;
        return (order[a.id] == null ? 1e9 : order[a.id]) - (order[b.id] == null ? 1e9 : order[b.id]);
      });
    }
    drawList(rooms, from);
  });
}
function drawList(rooms, from) {
  var box = $('rooms'); clear(box);
  $('listCount').textContent = rooms.length + (rooms.length === 1 ? ' room' : ' rooms') + (from ? ' · vacant first, then nearest to ' + from : '');
  if (!rooms.length) { box.appendChild(el('div', 'empty', 'No rooms match these filters.')); return; }
  rooms.forEach(function (r) {
    var c = el('div', 'card ' + r.status);
    var top = el('div', 'top'), left = el('div');
    left.appendChild(el('div', 'nm', r.name));
    left.appendChild(el('div', 'sub', r.building + ' · Floor ' + r.floor + ' · seats ' + r.capacity));
    top.appendChild(left);
    var right = el('div'); right.style.textAlign = 'right';
    right.appendChild(el('span', 'pill ' + r.status, STATUS_LABEL[r.status].toUpperCase()));
    var a = ago(r.minutesAgo);
    if (a) { var s = el('div', 'sub', a); right.appendChild(s); }
    top.appendChild(right); c.appendChild(top);
    var tags = el('div', 'tags');
    (r.amenities || []).forEach(function (n) { tags.appendChild(el('span', 'tag', n)); });
    if (likelyFree(r)) tags.appendChild(el('span', 'tag hint', 'Likely free (no class now)'));
    if (tags.children.length) c.appendChild(tags);
    c.appendChild(reportButtons(r.id));
    box.appendChild(c);
  });
}
['fBuilding', 'fAmenity', 'fStatus', 'fFrom'].forEach(function (id) { $(id).addEventListener('change', function () { loadList().catch(fail); }); });
$('fCap').addEventListener('input', debounce(function () { loadList().catch(fail); }, 350));
$('filters').addEventListener('submit', function (e) { e.preventDefault(); });
function debounce(f, ms) { var t; return function () { clearTimeout(t); t = setTimeout(f, ms); }; }

function report(roomId, occupied, holder) {
  if (!state.studentId) {
    toast('Choose who you are first (top right) or create a new student.', true);
    $('studentSelect').focus(); return;
  }
  var btns = holder.querySelectorAll('button');
  Array.prototype.forEach.call(btns, function (b) { b.disabled = true; });
  api('POST', '/availability', { roomId: roomId, studentId: state.studentId, occupied: occupied })
    .then(function () {
      toast('Thanks! Room reported as ' + (occupied ? 'occupied' : 'vacant') + '.');
      return refreshAll(true);
    }).catch(fail)
    .then(function () { Array.prototype.forEach.call(btns, function (b) { b.disabled = false; }); });
  setTimeout(function () { loadNotifications(true).catch(function () {}); }, 2500);
}

function loadPersonal(quiet) { return Promise.all([loadSubs(), loadNotifications(quiet)]); }
function loadSubs() {
  var box = $('subList'); clear(box);
  if (!state.studentId) { box.appendChild(el('p', 'muted small-text', 'Choose who you are to see your subscriptions.')); return Promise.resolve(); }
  return api('GET', '/students/' + state.studentId + '/subscriptions').then(function (list) {
    state.subs = list; clear(box);
    if (!list.length) box.appendChild(el('p', 'muted small-text', 'No subscriptions yet.'));
    list.forEach(function (s) {
      var row = el('div', 'sub-item'), t = el('div');
      t.appendChild(el('strong', '', s.building));
      var bits = [];
      if (s.minCapacity) bits.push('seats ' + s.minCapacity + '+');
      if (s.amenity) bits.push(s.amenity);
      t.appendChild(el('small', '', bits.length ? bits.join(' · ') : 'Any room'));
      var x = el('button', 'x', 'Delete'); x.type = 'button';
      x.addEventListener('click', function () {
        api('DELETE', '/subscriptions/' + s.id).then(loadSubs).then(function () { toast('Subscription removed.'); }).catch(fail);
      });
      row.appendChild(t); row.appendChild(x); box.appendChild(row);
    });
  });
}
$('subForm').addEventListener('submit', function (ev) {
  ev.preventDefault();
  if (!state.studentId) { toast('Choose who you are first (top right).', true); $('studentSelect').focus(); return; }
  var body = { studentId: state.studentId, building: $('sBuilding').value };
  var cap = parseInt($('sCap').value, 10); if (cap > 0) body.minCapacity = cap;
  if ($('sAmenity').value) body.amenityId = parseInt($('sAmenity').value, 10);
  api('POST', '/subscriptions', body).then(function () {
    toast('You will be notified when a matching room in ' + body.building + ' is vacant.');
    $('sCap').value = ''; $('sAmenity').value = ''; return loadSubs();
  }).catch(fail);
});

function seenKey() { return 'magis.seen.' + state.studentId; }
function loadNotifications(quiet) {
  var box = $('notifList');
  if (!state.studentId) {
    state.notifs = []; clear(box); box.appendChild(el('p', 'muted small-text', 'Choose who you are to see your notifications.'));
    $('notifCount').textContent = ''; updateBadge(); return Promise.resolve();
  }
  return api('GET', '/students/' + state.studentId + '/notifications').then(function (list) {
    var seen = parseInt(localStorage.getItem(seenKey()), 10);
    if (isNaN(seen)) { seen = 0; }
    var prevMax = state.notifs.reduce(function (m, n) { return Math.max(m, n.id); }, 0);
    if (state.seenNotifs != null) {
      list.filter(function (n) { return n.id > prevMax; }).forEach(function (n) { toast('🔔 ' + n.message); });
    }
    state.seenNotifs = true; state.notifs = list; state.seen = seen;
    drawNotifs();
  });
}
function drawNotifs() {
  var box = $('notifList'); clear(box);
  $('notifCount').textContent = state.notifs.length ? state.notifs.length + ' total' : '';
  if (!state.notifs.length) box.appendChild(el('p', 'muted small-text', 'Nothing yet. Subscribe below and you will see alerts here.'));
  state.notifs.forEach(function (n) {
    var d = el('div', 'note' + (n.id > state.seen ? ' new' : ''), n.message);
    d.appendChild(el('time', '', fmtTime(n.sentAt))); box.appendChild(d);
  });
  updateBadge();
}
function updateBadge() {
  var n = state.studentId ? state.notifs.filter(function (x) { return x.id > state.seen; }).length : 0;
  $('badge').hidden = n === 0; $('badge').textContent = n > 99 ? '99+' : n;
}
$('bellBtn').addEventListener('click', function () {
  $('notifPanel').scrollIntoView({ behavior: 'smooth', block: 'start' });
  if (state.studentId && state.notifs.length) {
    var max = state.notifs.reduce(function (m, n) { return Math.max(m, n.id); }, 0);
    localStorage.setItem(seenKey(), max); state.seen = max; updateBadge();
  }
});

function stamp() {
  state.lastOk = new Date();
  $('updatedText').textContent = 'Updated ' + state.lastOk.toLocaleTimeString([], { hour: 'numeric', minute: '2-digit', second: '2-digit' }) + ' · auto-refresh 30 s';
}
function refreshAll(quiet) {
  return Promise.all([refreshMap(), loadList(), loadPersonal(quiet)]).then(stamp).catch(function (e) {
    $('updatedText').textContent = 'Offline: ' + e.message;
    if (!quiet) fail(e);
  });
}
$('refreshBtn').addEventListener('click', function () { refreshAll(true); });
var rt, lastW = window.innerWidth;
window.addEventListener('resize', function () {
  clearTimeout(rt);
  rt = setTimeout(function () { if (!state.view.building && window.innerWidth !== lastW) drawCampus(); lastW = window.innerWidth; }, 200);
});

Promise.all([loadBuildings(), loadAmenities(), loadStudents()]).then(function () {
  return Promise.all([refreshMap(), loadList(), loadPersonal(true)]);
}).then(stamp).catch(function (e) {
  $('updatedText').textContent = 'Cannot connect';
  fail(e);
});
setInterval(function () { refreshAll(true); }, 30000);
})();

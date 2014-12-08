(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);throw new Error("Cannot find module '"+o+"'")}var f=n[o]={exports:{}};t[o][0].call(f.exports,function(e){var n=t[o][1][e];return s(n?n:e)},f,f.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
/*global __core__, SHAPE_TEST, event, __HINT_TOKEN_RENEW_INTERVAL__,
  __HINT_TOKEN_HEARTBEAT_INTERVAL__ */

(function () {
  // core.js
  var s = null, q = [];

  // q[i].__getdata__();  (sends periodic data)
  // q[i].__failed__(o);  (callback on a transport failure)
  // q[i].__success__(o); (callback on a transport success)
  // q[i].__submit__(o);  (callback on a convential submit)


  var flush = function () {
    // flush
    transmit(getdata());
  };


  var init = function (o, f) {
    // init
    q.push(o);
    if (f) {
      __core__.init = null;
    }
    return flush;
  };


  var success = function (o) {
    // success
    for (var i in q) {
      if (q[i].__success__ && typeof q[i].__success__ === 'function') {
        q[i].__success__(o);
      }
    }
  };


  var failed = function (o) {
    // failed
    for (var i in q) {
      if (q[i].__failed__ && typeof q[i].__failed__ === 'function') {
        q[i].__failed__(o);
      }
    }
  };


  var getdata = function () {
    // getdata
    var r = null;
    for (var i in q) {
      if (q[i].__getdata__ && typeof q[i].__getdata__ === 'function') {
        var d = q[i].__getdata__();
        for (var y in d) {
          if (r) {
            r += '&' + y + '=' + encodeURIComponent(d[y]);
          } else {
            r = y + '=' + encodeURIComponent(d[y]);
          }
        }
      }
    }
    return r;
  };


  var hasdata = function () {
    // hasdata
    var r = false;
    for (var i in q) {
      if (q[i].__hasdata__ && typeof q[i].__hasdata__ === 'function') {
        r = q[i].__hasdata__() || r;
      }
    }
    return r;
  };


  var submit = function (o) {
    // submit
    clearInterval(t);
    for (var i in q) {
      if (q[i].__submit__ && typeof q[i].__submit__ === 'function') {
        q[i].__submit__(o);
      }
    }
  };


  var transmit = function (o) {
    // transmit
    if (typeof XMLHttpRequest === 'undefined') {
      failed(-1);
      return false;
    }
    var x = new XMLHttpRequest();
    if (x) {
      if (x.__open__) {
        x.__open__.call(x, 'POST', '__HINT_TRANSPORT_URL__', true);
      } else {
        x.open('POST', '__HINT_TRANSPORT_URL__', true);
      }
      //Send the proper header information along with the request
      x.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
      x.onerror = function () {
        failed(this.status);
      };
      x.onreadystatechange = function () {
        /* eslint no-unused-expressions:0*/
        if (this.readyState === 4) {
          if (this.status === 200) {
            success(decodeURIComponent(this.responseText));
            return;
          }
          // call failure
          failed(this.status);
          t && clearInterval(t);
          s && clearInterval(s);
        }
      };
      if (x.__send__) {
        x.__send__.call(x, o);
      } else {
        x.send(o);
      }
      return true;
    }
    t && clearInterval(t);
    s && clearInterval(s);
    return false;
  };


  var submithook = function (o) {
    // submithook
    submit(o);
  };


  var eventhook = function (e) {
    // eventhook
    e = e ? e : event ? event : null;
    var o = e.target ? e.target : e.srcElement ? e.srcElement : null;
    submit(o);
  };


  var t = setInterval(function () {
      if (hasdata()) {
        if (s !== null) {
          clearInterval(s);
          s = null;
        }
        transmit(getdata());
      } else if (s === null) {
        transmit(getdata());
        s = setInterval(function () {
          transmit(getdata());
        }, __HINT_TOKEN_HEARTBEAT_INTERVAL__);
      }
    }, __HINT_TOKEN_RENEW_INTERVAL__);

  if (typeof SHAPE_TEST !== 'undefined' && SHAPE_TEST) {
    window.SHAPE = window.SHAPE || {};
    window.SHAPE.core = {
      transmit: transmit
    };
  }

  window.__core__ = {
    __submithook__: submithook,
    __eventhook__: eventhook,
    __init__: init
  };
}());

},{}]},{},[1]);(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);throw new Error("Cannot find module '"+o+"'")}var f=n[o]={exports:{}};t[o][0].call(f.exports,function(e){var n=t[o][1][e];return s(n?n:e)},f,f.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
/*global require, SHAPE_TEST, __ALL_THE_LULZ__,
  __BELONG_TO_US__, jsv */

var serialize = require('./common/serialize');

(function () {
  // browser.js
  var st = [], z = 0;

  st.push('__TELEMETRY_VERSION__');
  st.push(0);

  var browserFingerprint = function (win, doc, nav) {
    // browser_fprint
    var z = 0;
    z += win.postMessage ? 1 : 0;
    z += win.XDomainRequest ? 2 : 0;
    z += 'globalStorage' in win ? 4 : 0;
    z += 'sessionStorage' in win ? 8 : 0;
    z += win.removeEventListener ? 1 << 4 : 0;
    z += win.getComputedStyle ? 1 << 5 : 0;
    z += win.attachEvent ? 1 << 6 : 0;
    z += win.ActiveXObject ? 1 << 7 : 0;
    z += win.createPopup ? 1 << 8 : 0;
    z += win.dispatchEvent ? 1 << 9 : 0;
    z += 'localStorage' in win ? 1 << 10 : 0;
    z += win.addEventListener ? 1 << 11 : 0;
    z += win.detachEvent ? 1 << 12 : 0;
    z += win.fireEvent ? 1 << 13 : 0;
    z += doc.body && doc.body.innerText ? 1 << 14 : 0;
    z += doc.body && doc.body.innerHTML ? 1 << 15 : 0;
    z += doc.layers ? 1 << 16 : 0;
    z += doc.all ? 1 << 17 : 0;
    z += doc.getElementsByClassName ? 1 << 18 : 0;
    z += doc.frames ? 1 << 19 : 0;
    z += doc.getElementById ? 1 << 20 : 0;
    z += doc.querySelector ? 1 << 21 : 0;
    z += doc.images ? 1 << 22 : 0;
    z += doc.compatMode ? 1 << 23 : 0;
    z += doc.docMode ? 1 << 24 : 0;
    z += typeof nav.javaEnabled !== 'undefined' && nav.javaEnabled() ? 1 << 25 : 0;
    z += typeof nav.taintEnabled !== 'undefined' && nav.taintEnabled() ? 1 << 26 : 0;
    return z;
  };

  z = browserFingerprint(window, document, navigator);
  st.push(z);

  var hashCode = function (s) {
    // hashCode
    var z = 0;
    var x = typeof s;
    if (x !== 'boolean' && x !== 'number' && x !== 'string') {
      return z;
    }
    s = s.toString();
    if (s.length === 0) {
      return z;
    }
    for (var i = 0; i < s.length; ++i) {
      z = (z << 5) - z + s.charCodeAt(i);
      z = z & z;
    }
    return z;
  };


  var copy = function (x, y, r) {
    // copy
    for (var i in x) {
      var t = typeof x[i];
      if (t === 'function' || t === 'object' && x[i] != null) {
        continue;
      } else {
        var z = i;
        if (typeof i === 'string') {
          t = lut[hashCode(i)];
          if (t && (typeof r === 'undefined' || !r)) {
            z = t[0];
            if (t[1]) {
              if (t[1] === 1) {
                t = hashCode(x[i]);
                if (typeof vlut[t] === 'number') {
                  y[z] = vlut[t];
                  continue;
                }
              } else if (x[i].length > 9 && t[1] > 9) {
                t = t[1] - 10;
                if (Math.random() * 1023 < t) {
                  t = hashCode(x[i]);
                  y[z] = t;
                  continue;
                }
              }
            }
          }
          y[z] = x[i];
        }
      }
    }
  };


  z = [__ALL_THE_LULZ__];

  // Generate the attribute lookup table
  var lut = {};
  for (var i = 0; i < z.length; ++i) {
    var x = z[i];
    if (x[1]) {
      lut[x[0]] = [i, x[1]];
    } else {
      lut[x[0]] = [i];
    }
  }


  z = [__BELONG_TO_US__];

  // Generate the value lookup table
  var vlut = {};
  for (i = 0; i < z.length; ++i) {
    vlut[z[i]] = i;
  }


  if (window.opera) {
    navigator.opera = window.opera.version();
  }


  var deviceFingerprint = function (win, doc, scrn) {
    // device_fprint
    var z = [];
    z.push(win.innerHeight);
    z.push(win.innerWidth);
    z.push(win.outerHeight);
    z.push(win.outerWidth);
    z.push(win.screenX);
    z.push(win.screenY);
    z.push(win.pageXOffset);
    z.push(win.pageYOffset);
    z.push(scrn.height);
    z.push(scrn.availHeight);
    z.push(scrn.width);
    z.push(scrn.availWidth);
    z.push(scrn.colorDepth);
    z.push(scrn.pixelDepth);
    if (doc.body) {
      z.push(doc.body.clientHeight);
      z.push(doc.body.clientWidth);
    } else {
      z.push(-1, -1);
    }
    return z;
  };

  z = deviceFingerprint(window, document, screen);
  st.push(z);


  z = {};
  copy(navigator, z);
  st.push(z);


  var valueof = function (obj) {
    return typeof obj !== 'undefined' && obj ? obj : '';
  };


  var pluginCopy = function (obj, x) {
    // pcopy
    x.push(valueof(obj.name));
    x.push(valueof(obj.description));
    x.push(valueof(obj.filename));
    x.push(valueof(obj.version));
    var y = [];
    for (var i = 0; i < obj.length; ++i) {
      y.push(valueof(obj[i].type));
      y.push(valueof(obj[i].suffixes));
      y.push(valueof(obj[i].description));
    }
    x.push(y);
  };


  z = [];
  var y = lut[hashCode('plugins')];
  y = y && y[1] ? y[1] - 10 : 0;
  for (i = 0; i < navigator.plugins.length; ++i) {
    x = [];
    pluginCopy(navigator.plugins[i], x);
    if (Math.random() * 1023 < y) {
      z.push(hashCode(serialize(x)));
    } else {
      z.push(x);
    }
  }


  st.push(z);
  st.push(document.location.href);
  var jsver = function () {
    // jsver
    var x = document.getElementsByTagName('head')[0] || document.documentElement;
    for (var ver = 10; ver < 21; ver++) {
      var z = (ver / 10).toFixed(1);
      var obj = document.createElement('script');
      obj.setAttribute('language', 'javascript' + z);
      obj.text = 'jsv = ' + z + ';';
      x.insertBefore(obj, x.lastChild);
      x.removeChild(obj);
    }
    return jsv;
  };

  st.push(jsver());
  if (typeof XMLHttpRequest !== 'undefined') {
    z = new XMLHttpRequest();
    z.open('POST', '__HINT_TRANSPORT_URL__', true);
    z.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    //    z.onreadystatechange = function() { };
    z.send('__HINT_TELEMETRIC_POST_PARAM__=' + encodeURIComponent('__HINT_TELEMETRY_TOKEN__') + '&data=' + encodeURIComponent(serialize(st)));
  }

  if (typeof SHAPE_TEST !== 'undefined' && SHAPE_TEST) {
    window.SHAPE = window.SHAPE || {};
    window.SHAPE.browser = {
      deviceFingerprint: deviceFingerprint,
      browserFingerprint: browserFingerprint,
      serialize: serialize,
      hashCode: hashCode,
      pluginCopy: pluginCopy,
      jsver: jsver
     };
  }
}());

},{"./common/serialize":2}],2:[function(require,module,exports){
/* global module */
function serialize (a) {
  var s, i;
  switch (typeof a) {
  case 'string':
    return '"' + a.split('\n').join('\\n').split('\r').join('\\r').split('"').join('\\"') + '"';
  case 'object':
    if (a === null) {
      return 'null';
    }
    if (Object.prototype.toString.call(a) === '[object Array]') {
      for (i = 0, s = []; i < a.length; ++i) {
        s.push(serialize(a[i]));
      }
      return '[' + s.join(',') + ']';
    }
    s = [];
    for (i in a) {
      s.push(serialize(i) + ':' + serialize(a[i]));
    }
    return '{' + s.join(',') + '}';
  case 'undefined':
    return 'null';
  }
  return String(a);
}

module.exports = serialize;

},{}]},{},[1]);(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);throw new Error("Cannot find module '"+o+"'")}var f=n[o]={exports:{}};t[o][0].call(f.exports,function(e){var n=t[o][1][e];return s(n?n:e)},f,f.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
/* global module */
function serialize (a) {
  var s, i;
  switch (typeof a) {
  case 'string':
    return '"' + a.split('\n').join('\\n').split('\r').join('\\r').split('"').join('\\"') + '"';
  case 'object':
    if (a === null) {
      return 'null';
    }
    if (Object.prototype.toString.call(a) === '[object Array]') {
      for (i = 0, s = []; i < a.length; ++i) {
        s.push(serialize(a[i]));
      }
      return '[' + s.join(',') + ']';
    }
    s = [];
    for (i in a) {
      s.push(serialize(i) + ':' + serialize(a[i]));
    }
    return '{' + s.join(',') + '}';
  case 'undefined':
    return 'null';
  }
  return String(a);
}

module.exports = serialize;

},{}],2:[function(require,module,exports){
/*global require, __core__, __MCACHE_LIMIT__, __KCACHE_LIMIT__,
  __CCACHE_LIMIT__ */

var serialize = require('./common/serialize');

(function (c) {
  // user.js
  var st = [], mc = [];
  var kc = [], cc = [], tt = [], sm = [];

  var target = {};

  st.push('__TELEMETRY_VERSION__');
  st.push(1);
  st.push(tt);
  st.push(cc);
  st.push(kc);
  st.push(sm);

  var findpos = function (o) {
    // findpos
    var x = 0, y = 0;
    if (o.offsetParent) {
      do {
        x += o.offsetLeft;
        y += o.offsetTop;
        o = o.offsetParent;
      } while (o);
    }
    return [
      x,
      y
    ];
  };


  var targettable = function (o) {
    // targettable
    var z = findpos(o);
    var x = target[z];

    if (typeof x !== 'number') {
      var y = [];
      y.push(o.getAttribute('id') ? o.getAttribute('id') : '');
      y.push(o.getAttribute('name') ? o.getAttribute('name') : '');
      y.push(o.getAttribute('type'));
      y.push(o.getAttribute('hidden') ? 1 : 0);
      y.push(o.getAttribute('tagName'));
      target[z] = tt.length;
      x = tt.length;
      tt.push(y);
    }
    return x;
  };


  var onmousemove = function (e) {
    /*global event*/
    // onmousemove
    e = e ? e : event ? event : null;
    var z = new Date();
    var ts = z.getTime();

    if (mc.length) {
      z = mc.length - 1;
      mc[z] = ts - mc[z];
    }

    mc.push(e.clientY);
    mc.push(e.clientX);
    mc.push(ts);

    if (mc.length > __MCACHE_LIMIT__ * 3) {
      mc.shift();
      mc.shift();
      mc.shift();
    }
  };


  var f;
  var onkeydown = function (e) {
    // onkeydown
    e = e ? e : event ? event : null;
    var y = e.which || e.keyCode;
    f = false;

    if ((y < 49 && y !== 16 && y !== 17 && y !== 18) ||
       ((e.ctrlKey || e.metaKey) && y === 67) ||
       ((e.ctrlKey || e.metaKey) && y === 88) ||
       ((e.ctrlKey || e.metaKey) && y === 86)) {
      f = true;
      reckey(y, e);
    }
  };


  var onkeypress = function (e) {
    // onkeypress
    if (!f) {
      reckey(0, e);
    }
  };


  var reckey = function (keyCode, e) {
    // reckey
    e = e ? e : event ? event : null;
    var o = e.target ? e.target : (e.srcElement ? e.srcElement : null);
    var z = new Date();
    var ts = z.getTime();

    if (kc.length) {
      z = kc[0];
      z[0] = ts - z[0];
    }

    var y = [];
    y.push(ts);

    z = 0;
    z += e.altKey ? 1 : 0;
    z += e.ctrlKey ? 2 : 0;
    z += e.metaKey ? 4 : 0;
    z += e.shiftKey ? 8 : 0;

    y.push(z);

    y.push(keyCode);

    y.push(targettable(o));

    kc.unshift(y);
    if (kc.length > __KCACHE_LIMIT__) {
      kc.pop();
    }
  };


  var onclick = function (e) {
    // onclick
    e = e ? e : event ? event : null;
    var o = e.target ? e.target : e.srcElement ? e.srcElement : null;
    var z = new Date();
    var ts = z.getTime();

    if (cc.length) {
      z = cc[0];
      z[0] = ts - z[0];
    }

    var y = [];
    y.push(ts);
    y.push(e.clientX);
    y.push(e.clientY);
    y.push(e.button);
    y.push(targettable(o));
    y.push(mc.reverse());
    mc = [];
    cc.unshift(y);
    if (cc.length > __CCACHE_LIMIT__) {
      cc.pop();
    }
  };


  if (document.attachEvent) {
    document.attachEvent('onkeydown', onkeydown);
    document.attachEvent('onkeypress', onkeypress);
    document.attachEvent('onmousemove', onmousemove);
    document.attachEvent('onclick', onclick);
  } else if (document.addEventListener) {
    document.addEventListener('keydown', onkeydown);
    document.addEventListener('keypress', onkeypress);
    document.addEventListener('mousemove', onmousemove);
    document.addEventListener('click', onclick);
  } else {
    document.onkeydown = onkeydown;
    document.onkeypress = onkeypress;
    document.onmousemove = onmousemove;
    document.onclick = onclick;
  }


  var m = {};
  m.__getdata__ = function () {
    // getdata
    var r = serialize(st);
    st = [];
    st.push('__TELEMETRY_VERSION__');
    st.push(1);
    tt = [];
    target = {};
    st.push(tt);
    cc = [];
    st.push(cc);
    kc = [];
    st.push(kc);
    sm = [];
    st.push(sm);
    return { 'data': r };
  };


  m.__hasdata__ = function () {
    // hasdata
    return tt.length || cc.length || kc.length || sm.length;
  };


  m.__submit__ = function (o) {
    // submit
    var z = new Date();
    var ts = z.getTime();
    sm.push(ts);
    sm.push(mc.reverse());
    sm.push(o.__formid__);
    sm.push(o.getAttribute('action'));
    if (o.method === 'get') {
      flush();
      return;
    }
    if (!o.__telemetryflag__) {
      z = document.createElement('input');
      z.setAttribute('type', 'hidden');
      o.__telemetryflag__ = o.appendChild(z);
    }
    o.__telemetryflag__.setAttribute('name', '__BROWSER_TELEMETRY_NAME__');
    o.__telemetryflag__.setAttribute('value', serialize(st));
  };

  var flush = c.__init__(m);

}(__core__));

},{"./common/serialize":1}]},{},[2]);(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);throw new Error("Cannot find module '"+o+"'")}var f=n[o]={exports:{}};t[o][0].call(f.exports,function(e){var n=t[o][1][e];return s(n?n:e)},f,f.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
/*global SHAPE_TEST, __core__ */

(function (o) {

  // hinttoken.js
  var hinttoken = '__HINT_TOKEN__';
  var submitFlag = false;

  var m = {};
  m.__success__ = function (o) {
    // success
    hinttoken = o;
  };

  m.__failed__ = function (o) {
    // failed
    submitFlag = false;
  };

  m.__getdata__ = function () {
    // getdata
    return { '__HINT_TOKEN_NAME__': hinttoken };
  };

  m.__submit__ = function (o) {
    // submit
    if (!o.__tokenflag__) {
      var t = document.createElement('input');
      t.setAttribute('type', 'hidden');
      o.__tokenflag__ = o.appendChild(t);
    }
    o.__tokenflag__.setAttribute('name', '__HINT_TOKEN_NAME__');
    o.__tokenflag__.setAttribute('value', hinttoken);
    submitFlag = true;
  };


  var qins = function (o, a, b) {
    if (o[a]) {
      if (o[a] instanceof Array) {
        o[a].push(b);
      } else {
        o[a] = [ o[a], b ];
      }
    } else {
      o[a] = b;
    }
  };

  var qext = function (a, o) {
    if (o instanceof Array) {
      var b = '';
      for (var c in o) {
        b += (b.length ? '&' : '') + a + '=' + o[c];
      }
      return b;
    } else if (o != null) {
      return a + '=' + o;
    } else {
      return a;
    }
  };

  var qstr = function (o) {
    /*eslint no-unused-expressions:0*/
    var a = o.split('&');
    var b = null;
    while (a.length) {
      var c = a.pop();
      var d = c.split('=');
      (b || (b = {})) && qins(b, d[0], d[1] || null);
    }
    return b;
  };

  var strq = function (o) {
    var a = '';
    for (var b in o) {
      a += (a.length ? '&' : '') + qext(b, o[b]);
    }
    return a;
  };

  var anchor;
  var isSameOrigin = function (url) {
    var loc = window.location;

    anchor = anchor || document.createElement('a');
    anchor.href = url;

    return anchor.hostname === loc.hostname &&
      anchor.port === loc.port &&
      anchor.protocol === loc.protocol;
  };

  function isUrlEncoded(str) {
    return /^(\&?[\w+%]+(\=|\=[\w+%])?)+$/.test(str);
  }

  // NOTE: We're trusting the host JSON object, which is only
  //       available in IE8 and up. In order to support IE7
  //       and below (and to prevent using potentially-compromised
  //       host functions), we should inject JSON2 or some other
  //       JSON library in here.

  var isJSON = function (str) {
    if (window.JSON && JSON.parse) {
      try {
        JSON.parse(str);
        return true;
      } catch (e) {}
    }
    return false;
  };

  var appendTokenToJsonBody = function (body) {
    if (window.JSON && JSON.stringify && JSON.parse) {
      try {
        var json = JSON.parse(body);
        json.__HINT_TOKEN_NAME__ = hinttoken;
        return JSON.stringify(json);
      } catch (e) { }
    }
  };

  var appendTokenToUrlEncodedBody = function (body) {
    return body.replace(/\&?$/, '&__HINT_TOKEN_NAME__=' +
      encodeURIComponent(hinttoken).replace('%20', '+')
    );
  };

  var appendTokenToStringBody = function (body, mimeType) {
    if (body === '') {
      return body;
    }

    switch (mimeType) {
      // If mimeType is application/x-www-form-urlencoded _or_ application/json,
      // we assume the body is well formed.
      case 'application/x-www-form-urlencoded':
        return appendTokenToUrlEncodedBody(body);

      case 'application/json':
        return appendTokenToJsonBody(body);

      // If we are given text/plain, we treat the actual format as unknown,
      // and attempt to see if body is JSON _or_ URL encoded. If not, we treat
      // it as a standard text/plain body (newline separated key/val pairs).
      //
      // https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest/Using_XMLHttpRequest#Using_nothing_but_XMLHttpRequest

      case 'text/plain':
        if (isJSON(body)) {
          return appendTokenToJsonBody(body);
        } else if (isUrlEncoded(body)) {
          return appendTokenToUrlEncodedBody(body);
        } else {
          return body.replace(/\n?$/, '\n__HINT_TOKEN_NAME__=' + encodeURIComponent(hinttoken));
        }
    }

    return body;
  };

  var appendTokenToBody = function (body, mimeType) {
    /* eslint no-cond-assign:0*/
    var type = Object.prototype.toString.call(body);

    switch (type) {
      case '[object FormData]':
        body.append('__HINT_TOKEN_NAME__', hinttoken);
        break;

      case '[object String]':
        body = appendTokenToStringBody(body, mimeType);
        break;
    }

    return body;
  };

  if (window.XMLHttpRequest && XMLHttpRequest.prototype && XMLHttpRequest.prototype.send) {
    var requestMimeType = 'text/plain';
    var method = 'GET';

    XMLHttpRequest.prototype.__setRequestHeader__ = XMLHttpRequest.prototype.setRequestHeader;
    XMLHttpRequest.prototype.setRequestHeader = function (a, b) {
      if (a.toLowerCase() === 'content-type') {
        var x = b.indexOf(';');
        requestMimeType = b.substring(0, x > 0 ? x : b.length).toLowerCase();
      }

      this.__setRequestHeader__.call(this, a, b);
    };

    XMLHttpRequest.prototype.__open__ = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function (a, b) {
      if (!isSameOrigin(b)) {
        submitFlag = true;
      }

      method = a.toUpperCase();

      if (!submitFlag && method === 'GET' && b.indexOf('?') > 0) {
        b += '&__HINT_TOKEN_NAME__=' + encodeURIComponent(hinttoken);
        submitFlag = true;
      }

      return this.__open__.apply(this, arguments);
    };

    XMLHttpRequest.prototype.__send__ = XMLHttpRequest.prototype.send;
    XMLHttpRequest.prototype.send = function (d) {
      if (!submitFlag) {
        d = appendTokenToBody(d, requestMimeType);
      }

      this.__send__.call(this, d);
      submitFlag = false;
    };
  }

  if (typeof SHAPE_TEST !== 'undefined' && SHAPE_TEST) {
    window.SHAPE = window.SHAPE || {};
    window.SHAPE.hinttoken = {
      qstr: qstr,
      strq: strq,
      isSameOrigin: isSameOrigin,
      appendTokenToBody: appendTokenToBody,
      isJSON: isJSON,
      isUrlEncoded: isUrlEncoded
    };
  }

  o.__init__(m, true);
}(__core__));

},{}]},{},[1]);
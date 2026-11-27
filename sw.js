const C='academy-v3';const F=['./','./index.html','./manifest.json','./icon-192.png','./icon-512.png'];
self.addEventListener('install',e=>{e.waitUntil(caches.open(C).then(c=>c.addAll(F)));self.skipWaiting()});
self.addEventListener('activate',e=>{e.waitUntil(caches.keys().then(k=>Promise.all(k.filter(x=>x!==C).map(x=>caches.delete(x)))));self.clients.claim()});
// 공유 서버(Firebase)와 주고받는 요청은 건드리지 않는다: 실시간 연결이 끊기지 않고, 오래된 응답이 저장되지 않게 한다
self.addEventListener('fetch',e=>{if(e.request.method!=='GET'||/firebasedatabase\.app|firebaseio\.com/.test(e.request.url))return;e.respondWith(fetch(e.request).then(r=>{const cp=r.clone();caches.open(C).then(c=>c.put(e.request,cp)).catch(()=>{});return r}).catch(()=>caches.match(e.request)))});

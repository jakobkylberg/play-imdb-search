### Komigång

Starta play:
```
./activator run
```

Starta play i debugläge, lyssnandes på port 9999:
```
./activator -jvm-debug 9999 run
```

Sök via GUI:
http://localhost:9000/searchGUI

Sök och få svar som JSON:
http://localhost:9000/searchSync?query=[söksträng]

### Lab

1. Skriv om den synkrona sökningen efter titlar till att bli icke-blockerande (tips: kedja promises)

2. Den titelinformation man får i sökresultaten från iMDb är begränsad, berika resultaten genom att anropa oMDb:s api och hämta mer information

### Länkar

Play framework, dokumentation:
https://www.playframework.com/documentation/2.3.x/Home

Play framework, mer information om Play's WS-klient och promises
https://www.playframework.com/documentation/2.3.x/JavaWS

iMDb, sök-api som söker efter träffar på iMDb-titlar:
http://www.imdb.com/xml/find?json=1&nr=1&tt=on&q=[söksträng]

oMDb, titel-api som ger mer utförlig information utifrån en titel:
http://www.omdbapi.com/?i=[imdb-id]&plot=short&r=json

### Mer Play

Mallar och exempel för Play framework, ladda ner Typesafe activator:
https://www.playframework.com/download

Starta med
```
activator ui
```
för starta activator-GUI:et och skapa önskat exempel eller mall
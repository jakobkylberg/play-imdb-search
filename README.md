### Länkar:

Play framework, dokumentation:
https://www.playframework.com/documentation/2.3.x/Home

Play framework, mer information om Play's WS-klient och futures 
https://www.playframework.com/documentation/2.3.x/JavaWS

Git-repo:

iMDb, sök-api:
http://www.imdb.com/xml/find?json=1&nr=1&tt=on&q=<sökterm>

oMDb, titel-api:
http://www.omdbapi.com/?i=<imdb-id>&plot=short&r=json

### Lab:

1. Skriv om den synkrona sökningen efter titlar till att bli icke-blockerande (tips: kedja futures)

2. Den titelinformation man får i sökresultaten från iMDb är begränsade, berika resultaten genom att anropa oMDb:s api och hämta mer information

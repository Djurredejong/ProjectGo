# ProjectGo

Creëer een clone van deze repository in Eclipse of een andere Java IDE. Als je een potje Go wilt spelen tegen de computer (die enkel zal passen) of tegen een andere speler op één computer, hoef je enkel de ‘game’ en ‘exceptions’ packages te importeren. Run de class GoGame en volg de instructies in de console. 

Als je via een netwerk een potje Go wilt spelen, importeer dan ook de ‘client’, ‘server’ en ‘protocol’ packages. Run de class Server en volg de instructies in de console. Run vervolgens de class Client om een eerste speler te creëren, en volg ook hier de instructies in de console. Voor een succesvol spelbegin is het van belang dat deze client de verbinding niet verliest totdat er een connectie met een tweede client bestaat. Run de class Client nogmaals om een tweede speler te creëren en volg wederom de instructies in de console. De twee clients spelen nu het spel Go tegen elkaar! 

Doordat de implementatie nog verre van volmaakt is, zal voor elk nieuw spel een nieuwe Server gestart en twee nieuwe client-server verbindingen gemaakt moeten worden. Het spel op een niet-conventionele manier beëindigen, zal in een error resulteren. Een spel eindigt op een conventionele manier zodra het spelbord vol is. Twee keer passen beeindigt het spel helaas nog niet, tenzij je het op de eerste manier, via de GoGame class, speelt. 

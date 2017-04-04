# Calcul de créneaux horaires libres

Voici une modeste (et je l'espère correcte) réponse au test *Java* de **Inch**.

Le test a été téléchargé depuis [ce GIST](https://gist.github.com/Uelb/1ad5ac1fea71351c83d4be6c3d86f072).
L'énoncé du test est contenu dans le fichier `README_original.md` dans ce même répertoire.

## Remarque sur la durée d'un créneau horaire

L'énoncé de **Inch** ne précise pas la durée d'un créneau horaire ( *window* ou *slot* ). D'après les exemples donnés, ils ont une durée de 30 minutes. Mon programme récupère cette durée en tant que paramètre avec 30 minutes par défaut (si le paramètre n'est pas fournis).

## Construction du projet

### Prérequis

Mon projet nécessite [Maven](maven.apache.org) pour être construit.

### Commande

La commande pour construire le programme est `mvn install`. Elle :

- Compile le projet
- Exécute les tests unitaires
- _(comme le projet n'a pas de dépendance, pas besoin de les encapsuler)_
- Encapsule le programme dans une archive *JAR*

### Test unitaires

Un test unitaire présent dans le fichier `Content.java` consiste à vérifier le cas de l'[exemple indiqué dans l'énoncé du problème](https://gist.github.com/Uelb/1ad5ac1fea71351c83d4be6c3d86f072#file-content-java).

Il existe de plus d'autres tests pour vérifier la qualité du programme.

## Exécution du projet
 
### Prérequis

Nécessite un *JRE* d'une version 8 ou plus.

(En effet, le programme contient une expression _lanbda_ et utilise le _package java.time_).

### Commande

Comme le projet n'a pas de dépendance, il n'est pas nécessaire de définir de *classpath* pour l'exécution. Ainsi la commande suivante suffit :

```
java -jar target/test-inch-0.1-SNAPSHOT.jar
```

### Usage

Le programme ouvre une console. Une aide s'affiche alors en anglais. Elle accepte les commandes suivantes :

- `help` : donne des informations similaires à ce qui est inclus dans cette section.
- `quit` : quitte le programme
- `opening {start} {end} {weekly}` : Ajoute un créneau d'ouverture. Nécessite le moment de début de la période de temps à considérer au format ISO `YYYY-MM-ddTHH:mm` et le moment de fin de la période de temps à considérer au même format. Si le troisième argument est fournis et a pour valeur `true` avec le créneau est hebdomadaire.
- `closed {start} {end} {weekly}` : Ajoute un créneau d'indisponibilité. Nécessite les mêmes arguments que la commande `opening`.
- `out {start} {end} {duration}` : Nécessite le moment de début de la période de temps à considérer au format ISO `YYYY-MM-ddTHH:mm`, le moment de fin de la période de temps à considérer au même format et la durée d'un créneau horaire en minutes. Ce dernier argument est optionel et est de 30 minutes par défaut. Il renvoi les créneaux libres sous forme d'un message au format JSON. Ce message aggrège les créneaux horaires par date et dans l'ordre chronologique.

### Exemple

Pour reproduire le cas de test (qui est aussi présent sous forme de test unitaire), il faut exécuter les commande suivantes :
```
open 2016-07-01T10:30 2016-07-01T14:00 true
busy 2016-07-08T11:30 2016-07-08T12:30 false
out 2016-07-04T10:00 2016-07-10T10:00 30
```
Le programme affichera alors :
```
{
"2016-07-08": ["10:30", "11:00", "12:30", "13:00", "13:30"]
}
```



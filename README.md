# Solveur de CSP

Pour tester nos expérimentations, 3 classes sont disponibles :

1. Application
2. ExperimentationLauncher
3. ExperimentalNetworksGenerator

## Application

Cette classe permet de tester simplement un réseau en réalisant un backtrack() ou backtrackAll() sur celui ci.

Il suffit de spécifier le path du fichier dans la variable *String filename* se trouvant au début du main().

## ExperimentationLauncher

C'est la classe principale servant à réaliser les tests.

Elle va chercher le dossier de trouvant en *String dirName*, et va récupérer les fichiers dans celui-ci en fonction des paramètres suivants :

- La dureté
    - *int minHardness* pour dureté minimum
    - *int maxHardness* pour dureté maximum
    - *int hardnessIncrement* pour le pas de dureté entre chaque niveau
- Le nombre de réseaux par niveau de dureté
    - *int networksPerHardnessIncrement*
    
Le main de cette classe va réaliser un benchmark complet en fonction des paramètres fournis, à savoir :

- *int testsPerNetwork* nombre de tests par réseau, spécifié à 5 dans les consignes
- *double timeOut* permettant, si différent de 0, de spécifier une limite de temps par test
- *boolean printAll* permettant de print tous les résultats si laissé à **true**, et qui limitera l'affichage aux moyennes par dureté et à la moyenne finale si mis à **false**

Avec les paramètres actuels, et si le dossier "ReseauxExp/Var20_Dom10_Densite0.2" a bien été récupéré et est bien constitué de tous les réseaux de base (170 réseaux), il suffit normalement de lancer la classe pour effectuer un benchmark complet.

Afin de tester avec ou sans optimisation :

- Dans la classe ***CSP***, dans la méthode *consistant*, changer violation par violationOpt (ou l'inverse) pour tester avec ou sans la méthode de détection de viol de contrainte optimisée;
- Dans la classe ***Network***, à la fin du **constructeur**, commenter/décommenter l'appel à *maxDegree()* pour tester avec ou sans la méthode d'heuristique des variables;
- Dans la classe ***Network***, à la fin du **constructeur**, commenter/décommenter l'appel à *AC3()* pour tester avec ou sans la méthode qui permet de rétablir l'arc-consistance.

## ExperimentalNetworksGenerator

Cette classe nous a permis d'automatiser la création de réseaux. Les paramètres sont très similaires à ceux de la classe ***ExperimentationLauncher***. Ils sont utilisés pour générer toutes les commandes nécessaires à la création des réseaux ayant les paramètres spécifiés.

Une fois le programme lancé, toutes les commandes sont affichées dans la console, et il suffit de les copier/coller dans une invite de commande située au niveau du générateur de CSP binaire **urbcsp.exe** **FOURNI AVEC NOTRE DOSSIER** (des modifications ont été apportées) afin de générer les réseaux.

### IMPORTANT :
- Toutes les commandes ont été générées et testées sous **Windows 10** et non Linux. Les commandes pour Linux seront légèrement différentes.
- Nous avons **modifié le générateur de réseau urbcsp.c** de façon à ce que les randoms générés à chaque lancement du programme avec les mêmes paramètres soient **différents** (ce qui n’était pas le cas avant). De cette façon, pour générer 10 réseaux **différents** mais ayant les **mêmes paramètres**, il suffit générer 10 commandes identiques à l’exception du nom du fichier généré (ce que fait automatiquement ***ExperimentalNetworksGenerator***), plutôt que de changer la dernière valeur passée à **urbcsp.exe** de 1 à 10 et d’avoir à manuellement séparer le fichier en 10.

**Nous associons au rendu de projet ce générateur urbcsp.c ainsi que l’exécutable associé urbcsp.exe (si besoin de le tester).**
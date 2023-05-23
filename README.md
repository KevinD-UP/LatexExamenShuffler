# LaTeXExamenShuffler

## Pré-requis

- java 20
- maven 3.8.1
- pdfLaTeX ou LuaLaTeX ou XeLaTeX

## Compiler le projet

Lancer depuis la racine du projet la commande `mvn package`

## Lancer le projet

Lancer depuis le répertoire du projet la commande 
```
./latexExamenShuffler.sh cheminInput nbrVariations
```

nbrVariations variations du fichier .tex de chemin inputPath seront alors générées. Elles seront nommées "generatedI.tex" , I allant de 1 au nombre de variation demandé. 
Par défaut les variations se situeront dans le dossier `output/`. 

Vous pouvez préciser avec l'option `--output-dir outputDirPath` le répertoire dans lequel vous souhaitez que les variations soient générées.

Vous pouvez également préciser avec l'option `--output-filename outputFilename` le nom des fichiers générés.

## Fonctionnalités

- Les variations sont des permutations sur l'ordre des exercices.

- Pour être accepté en entrée, un document .tex doit définir 

```latex
\newtheorem{exi}{}
\newenvironment{exo}{\begin{exi}\em}{\end{exi}}
```
avant le premier exercice.

- Un exercice doit être compris entre les lignes `\begin{exo}` et  `\end{exo}`

- Tout ce qui se trouve entre une ligne `\end{exo}` et une ligne `\begin{exo}` ne sera pas reporté sur les variations générées.

- Il est possible d'indiquer qu'un exercice doit toujours être à la même position en écrivant le commentaire `%fixed` directement collé à `\begin{exo}`.
- Il est possible de choisir le compilateur LaTeX que l'on souhaite utiliser avec l'option `--compiler nom-du-compilateur`. Par défaut le compilateur utilisé est pdfLaTeX. Les compilateurs supportés sont : pdfLaTeX, LuaLaTeX ou XeLaTeX

## Auteurs

- Kévin Dang
- Alexandre Sabri
- Robin Arnoux
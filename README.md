# Visual Analytics Project

Our Visual Analytics tool to help in the case of missing employees from GAStech.
This is the product of Group 15 consiting of Oana Radu, Deepthy Unnikrishnan, Yvan Putra Satyawan.

## Building

1. Install wordcloud and related package in conda:
    `conda install -c conda-forge wordcloud scikit-learn pandas numpy plotly gensim matplotlib`
2. Install scikit-learn package in conda:
   `conda install scikit-learn`
   `conda install -c plotly plotly`
3. set `conda path` in environment variables.
4. run `gradlew clean build` from project root path.

## Running

Use the command `java -jar build/libs/shadow.jar` to open the visualization tool.

The following arguments are possible:
```bash
-p --python <python executable>  Python executable to use e.g., conda run -n visual_analytics python 
-r --root   <root directory>     Root directory of the project.
-d --data   <data directory>     Data directory of the project.
```

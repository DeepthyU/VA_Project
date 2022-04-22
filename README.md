# VA_Project
VA project to analyse GasTech missing employee data


To build:
1. Install wordcloud and related package in conda:
    `conda install -c conda-forge wordcloud scikit-learn pandas numpy plotly gensim matplotlib`
2. Install scikit-learn package in conda:
   `conda install scikit-learn`
   `conda install -c plotly plotly`
3. set `conda path` in environment variables.
4. run `gradlew clean build` from project root path.

Use the command `java -jar build/libs/shadow.jar` to open the visualization tool.

The following arguments are possible:
```bash
-p --python <python executable>  Python executable to use e.g., conda run -n visual_analytics python 
-r --root   <root directory>     Root directory of the project.
-d --data   <data directory>     Data directory of the project.
```

import sys
import matplotlib.pyplot as plt
import json
from wordcloud import WordCloud, STOPWORDS
import calendar
import time
import gensim

#print("Hello Baeldung Readers!!")
#print(f"Name of the script      : {sys.argv[0]=}")
#print(f"Arguments of the script : {sys.argv[1:]=}")	

arg = sys.argv[1]
#print(f"Arguments:", arg)
score = 0
if len(sys.argv) > 2:
    score = sys.argv[2]

f = open(arg, "r")
text = f.read()


all_stopwords = gensim.parsing.preprocessing.STOPWORDS
local_stopwords = set()
local_stopwords.update(STOPWORDS)
local_stopwords.update(all_stopwords)

# Generate word cloud
wordcloud = WordCloud(normalize_plurals=True, include_numbers=False, collocation_threshold=30, width = 3000, height = 2000, random_state=1, background_color='black', colormap='Set2', collocations=False, stopwords = local_stopwords).generate(text)
    
#wordcloud = WordCloud(width = 3000, height = 2000, random_state=1, background_color='black', colormap='Set2', collocations=False, stopwords = STOPWORDS).generate(text)
# Plot
# Set figure size
#plt.figure(figsize=(40, 30))
# Display image
#plt.imshow(wordcloud) 
# No axis details
#plt.axis("off");


gmt = time.gmtime()
ts = calendar.timegm(gmt)
filename = "article"+str(ts)
    
# Save image
# wordcloud.to_file(filename+".png")
dict = wordcloud.words_
#word_list=list(dict.keys())
words_list = []
for key in dict:
    if float(dict[key]) >= float(score):
	    words_list.append(key)
with open(filename+".txt", "w") as outfile:
		outfile.write("##".join(words_list))

print(filename)


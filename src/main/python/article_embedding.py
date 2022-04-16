"""Article Embedding.

Produces a TSNE embedding of every article.
"""
from argparse import ArgumentParser
from pathlib import Path
import json

import numpy as np
import pandas as pd
from sklearn.manifold import TSNE
from sklearn.feature_extraction.text import TfidfVectorizer
import plotly.express as px


def parse_args():
    p = ArgumentParser(description='Produces an embedding based on article '
                                   'contents')

    p.add_argument('PATH', type=Path,
                   help='Path to the `article_list.json` file.')
    p.add_argument('--out', type=Path,
                   help='Output nodes and edges JSON to a file')

    return p.parse_args()


def read_articles(fp: Path) -> pd.DataFrame:
    """Reads all given articles and turns them into a table."""
    articles = {'fileName': [],
                'publication': [],
                'pubId': [],
                'title': [],
                'date': [],
                'content': []}

    pub_ids = {}

    with open(fp) as file:
        for line in file:
            data = json.loads(line)
            for key in articles.keys():
                try:
                    if key == 'content':
                        content = data[key] \
                            .strip() \
                            .removesuffix(']') \
                            .removeprefix('[')
                        articles[key].append(content)
                    elif key == 'publication':
                        if data[key] not in pub_ids:
                            # Add to pub_ids
                            pub_ids[data[key]] = len(pub_ids)  # Count as ID
                        articles[key].append(data[key])
                        articles['pubId'].append(pub_ids[data[key]])
                    elif key == 'pubId':
                        pass
                    else:
                        articles[key].append(data[key])
                except KeyError:
                    articles[key].append("Unknown")

    return pd.DataFrame.from_dict(articles)


def prepare_embeddings(embedded: np.ndarray,
                       articles: pd.DataFrame) -> pd.DataFrame:
    """Prepares embeddings by adding data to them for visualization."""
    embedded = pd.DataFrame(embedded)
    output = articles.drop('content', axis=1)
    output['x'] = embedded[0].tolist()
    output['y'] = embedded[1].tolist()

    return output


def main():
    args = parse_args()

    articles = read_articles(args.PATH)

    vectorized = TfidfVectorizer().fit_transform(articles['content'].tolist())
    embedded = TSNE(learning_rate='auto', random_state=0,
                    verbose=0, n_jobs=-1,
                    perplexity=10.).fit_transform(vectorized)
    embedded = prepare_embeddings(embedded, articles)
    fig = px.scatter(embedded, x='x', y='y', color='publication',
                     hover_name='title', hover_data=['date'])
    fig.show()

    if args.out is not None:
        print(f"Writing out TSNE coordinates to {args.out}...")
        embedded.to_csv(args.out, index=False)


if __name__ == '__main__':
    main()

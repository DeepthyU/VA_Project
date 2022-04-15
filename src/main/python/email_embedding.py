from argparse import ArgumentParser
from pathlib import Path

import numpy as np
import pandas as pd
from sklearn.manifold import TSNE
import plotly.express as px

from email_grapher import get_all_users, process_to_col


def parse_args():
    p = ArgumentParser(description='Produces an embedding based on emails sent')

    p.add_argument('PATH', type=Path,
                   help='Path to the `email headers.csv` file.')
    p.add_argument('EXCEL', type=Path,
                   help='Path to the `EmployeeRecords.xlsx` file')
    p.add_argument('--out', type=Path,
                   help='Output nodes and edges JSON to a file')

    return p.parse_args()


def produce_one_hot(users: dict, emails: pd.DataFrame) -> pd.DataFrame:
    """Produces one hot encoding for users based on email thread membership

    Args:
        users: Users at the company
        emails: All emails sent at the company.
    Returns:
        Emails as one-hot encoded features with the user as the key
    """
    # First clean up emails by removing `RE: `/`re: `
    emails['Subject'] = emails['Subject'].str.removeprefix('RE: ',)

    # List of all unique subject lines
    subjects = emails['Subject'].unique().tolist()

    # Turn recipients into a list
    emails['To'] = emails['To'].apply(lambda x: process_to_col(x))

    one_hot_dict = {}

    # Make a list of user dictionaries
    for user, data in users.items():
        # This is going to be horribly inefficient but I don't want to think
        # much
        d = {'Name': data['Name'],
             'Department': data['Department'], 'DeptId': data['DeptId']}

        for subject in subjects:
            d[subject] = 0
        one_hot_dict[user] = d

    # Now iterate through every email
    for row in emails.to_dict('index').values():
        f = row['From']       # From
        r = row['To']         # Recipient
        sub = row['Subject']  # Subject

        if one_hot_dict[f][sub] == 0:
            one_hot_dict[f][sub] += 1

        for recipient in r:
            if one_hot_dict[recipient][sub] == 0:
                one_hot_dict[recipient][sub] += 1

    return pd.DataFrame.from_dict(one_hot_dict, orient='index')


def one_hot_to_np(df: pd.DataFrame) -> np.ndarray:
    """Turns the one hot encoded email subjects to a numpy array.

    Drops the index (Email), Name, Department, and DeptId columns.
    """
    numeric_df = df.drop(['Name', 'Department', 'DeptId'], axis=1)
    return np.array(numeric_df)


def plot_tsne(arr: np.ndarray, df: pd.DataFrame):
    """Plots the TSNE result using Plotly Express."""
    # print(df['DeptId'])
    embedded = pd.DataFrame(arr)
    embedded['DeptId'] = df['Department'].tolist()
    embedded['Name'] = df['Name'].tolist()
    fig = px.scatter(embedded, x=0, y=1, color='DeptId',
                     hover_name='Name')
    fig.show()


def main():
    args = parse_args()

    headers = pd.read_csv(args.PATH, usecols=['From', 'To', 'Subject'])
    users = get_all_users(headers, args.EXCEL)

    one_hot = produce_one_hot(users, headers)
    one_hot_np = one_hot_to_np(one_hot)

    print("Running TSNE...")
    users_embedded = TSNE(learning_rate='auto', random_state=0,
                          verbose=0, n_jobs=-1).fit_transform(one_hot_np)
    plot_tsne(users_embedded, one_hot)


if __name__ == '__main__':
    main()

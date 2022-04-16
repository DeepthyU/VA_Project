import pandas as pd
from pathlib import Path
from argparse import ArgumentParser
import json
import numpy as np
import plotly.express as px


def parse_args():
    p = ArgumentParser(description='Returns nodes and edges on the email graph')

    p.add_argument('PATH', type=Path,
                   help='Path to the `email headers.csv` file.')
    p.add_argument('EXCEL', type=Path,
                   help='Path to the `EmployeeRecords.xlsx` file')
    p.add_argument('--out', type=Path,
                   help='Output nodes and edges JSON to a file')

    return p.parse_args()


def process_to_col(text: str) -> list:
    """Process the `To` column into a list of recipients.

    Remove quotation marks, spaces, lower, and split into a list
    """
    texts = text \
        .strip('"') \
        .strip(" ") \
        .split(",")
    return [t.strip() for t in texts]


def get_all_users(df: pd.DataFrame, xlsx: Path) -> dict:
    """Gets all users involved in email exchanges.

    Returns:
        A dictionary with email as the key and a dictionary as its value with
        keys ('Name', 'Id', 'Department').
    """
    users = set()
    for user in df['From'].tolist():
        users.add(user.strip())

    for recipients in df['To'].tolist():
        for recipient in process_to_col(recipients):
            users.add(recipient)

    # Open the excel file
    emp_recs = pd.read_excel(xlsx, "Employee Records",
                             usecols=['LastName', 'FirstName',
                                      'CurrentEmploymentType',
                                      'EmailAddress'])

    for col in emp_recs.columns.tolist():
        emp_recs[col] = emp_recs[col].str.strip()

    emp_recs['Name'] = emp_recs['FirstName'] + ' ' + emp_recs['LastName']
    emp_recs = emp_recs.drop(['FirstName', 'LastName'], axis=1)
    emp_recs['Id'] = emp_recs.index
    emp_recs = emp_recs.set_index('EmailAddress')
    emp_recs = emp_recs.rename(columns={'CurrentEmploymentType': 'Department'})

    # Give every department an ID
    dept_id_map = {d: i for i, d in enumerate(emp_recs['Department'].unique())}
    emp_recs['DeptId'] = emp_recs.apply(
        (lambda row: dept_id_map[row['Department']]), axis=1)

    # Special handling of unknown
    unknown_user_id = len(emp_recs)
    user_info = {}

    for user in list(users):
        try:
            user_info[user] = emp_recs.loc[user].to_dict()
        except KeyError:
            user_info[user] = {
                'Name': user,
                'Id': unknown_user_id,
                'Department': 'Unknown',
                'DeptId': len(dept_id_map)
            }
            unknown_user_id += 1

    return user_info


def generate_edges(df: pd.DataFrame, user_info: dict) -> np.ndarray:
    """Generates a list of dictionary entries.

    Returns:
        3-dimensional adjacency matrix where the depth represents the time
        dimension.
    """
    dfd = df.to_dict('split')  # [D]ata[F]rame as [D]ictionary

    # Get unique dates to know depth of the array
    date_map = {date: depth
                for depth, date
                in enumerate(df['Date'].dt.date.unique().tolist())}

    # adj_matrix takes has shape (sender, recipient, date)
    adj_matrix = np.full((len(user_info), len(user_info), len(date_map)),
                         fill_value=0)

    for i in range(len(df)):
        data = dfd['data']
        f = data[i][0]  # f since from is a reserved keyword
        to = process_to_col(data[i][1])
        date = data[i][2]

        for recipient in to:
            f_idx = user_info[f]['Id']
            r_idx = user_info[recipient]['Id']
            d_idx = date_map[date.date()]

            adj_matrix[f_idx, r_idx, d_idx] += 1
    return adj_matrix


def np_to_dict(arr: np.ndarray) -> dict:
    """Converts a numpy array into a dictionary.

    We need this, as bad as it seems, to make a reasonable JSON.
    """
    out = {}
    for i in range(arr.shape[0]):
        j_dict = {}
        for j in range(arr.shape[1]):
            k_dict = {}
            for k in range(arr.shape[2]):
                k_dict[k] = arr[i, j, k]
            j_dict[j] = k_dict
        out[i] = j_dict
    return out


def verify_equivalency(adj_matrix, dict_matrix):
    for i in range(adj_matrix.shape[0]):
        for j in range(adj_matrix.shape[1]):
            for k in range(adj_matrix.shape[2]):
                error_msg = (f'Not equal at: '
                             f'{adj_matrix[i, j, k]=}, '
                             f'{dict_matrix[i][j][k]=}')
                assert adj_matrix[i, j, k] == dict_matrix[i][j][k], error_msg


if __name__ == '__main__':
    args = parse_args()

    headers = pd.read_csv(args.PATH, parse_dates=['Date'])

    users = get_all_users(headers, args.EXCEL)
    adj_matrix = generate_edges(headers, users)

    # Normalize matrix
    # Use a log scale then min-max to 0-255
    adj_matrix = np.log(adj_matrix + 1)  # + 1 to avoid log(0)
    adj_matrix /= adj_matrix.max() / 255.


    # Plot adjacency matrix counts
    adj_sums = np.sum(adj_matrix, axis=2)
    flattened_matrix = adj_sums.flatten()
    fig = px.histogram(flattened_matrix)
    fig.show()

    fig = px.imshow(adj_sums)
    fig.show()

    dict_matrix = np_to_dict(adj_matrix)
    verify_equivalency(adj_matrix, dict_matrix)

    output = {'emailNameIdMap': users,
              'edges': dict_matrix,
              'edgesShape': adj_matrix.shape}

    if args.out is not None:
        with open(args.out, 'w') as out_file:
            json.dump(output, out_file)
    else:
        print(json.dumps(output))

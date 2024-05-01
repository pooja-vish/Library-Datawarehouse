import pymysql
def fetch_data_from_source_table(source_db, table_name):
    try:
        source_conn = pymysql.connect(host='localhost', user='root', password='*****', db=source_db)
        source_cursor = source_conn.cursor()
        if table_name == 'members':
            source_cursor.execute('SELECT mid, mName, mPhoneNo FROM ' + table_name)
        elif table_name == 'books':
            source_cursor.execute('SELECT bid, bName, noOfCopies, bAuthor FROM ' + table_name)
        elif table_name == 'borrowers':
            source_cursor.execute('SELECT mid, bid, transType, bDate FROM ' + table_name)
        output = source_cursor.fetchall()
        source_cursor.close()
        source_conn.close()
        return output
    except pymysql.Error as e:
        print("Error fetching data from source table:", e, source_db, table_name)
        
def insert_date_into_dates_table(source_db):
    try:
        source_conn = pymysql.connect(host='localhost', user='root', password='*****', db=source_db)
        source_cursor = source_conn.cursor()
        destination_conn = pymysql.connect(host='localhost', user='root', password='******', db='library_dw')
        destination_cursor = destination_conn.cursor()
        source_cursor.execute('SELECT distinct bDate FROM borrowers')
        result = source_cursor.fetchall()
        for date in result:
                year_query = 'SELECT YEAR(%s)'
                month_query = 'SELECT MONTH(%s)'
                day_query = 'SELECT DAY(%s)'
                destination_cursor.execute(year_query, (date,))
                year_result = destination_cursor.fetchone()[0]
                destination_cursor.execute(month_query, (date,))
                month_result = destination_cursor.fetchone()[0]
                destination_cursor.execute(day_query, (date,))
                day_result = destination_cursor.fetchone()[0]
                query = 'INSERT INTO dw_dates(dwYear, dwMonth, dwDay) VALUES (%s, %s, %s)'
                destination_cursor.execute(query, (year_result, month_result, day_result))
                destination_conn.commit()
    except pymysql.Error as e:
        # Rollback the transaction on error
        destination_conn.rollback()
        print("Error inserting data into target table: table_name", e)

    finally:
        # Close cursor and connection
        source_cursor.close()
        source_conn.close()
        destination_cursor.close()
        destination_conn.close()

def insert_data_into_target_table(output, source_db, table_name):
    try:
        source_conn = pymysql.connect(host='localhost', user='root', password='******', db=source_db)
        source_cursor=source_conn.cursor()
        destination_conn = pymysql.connect(host='localhost', user='root', password='******', db='library_dw')
        destination_cursor = destination_conn.cursor()
        print(table_name)
        if table_name == 'books':
            sql = 'INSERT INTO books (bId, bName, noOfCopies, bAuthorName) VALUES (%s, %s, %s, %s)'
            destination_cursor.executemany(sql, output)
            destination_conn.commit()
        elif table_name == 'members':
            sql = 'INSERT INTO members (mid, mName, mPhone) VALUES (%s, %s, %s)'
            destination_cursor.executemany(sql, output)
            destination_conn.commit()
        elif table_name == 'borrowers':
            for row in output:
                year_q = 'SELECT YEAR(%s)'
                month_q = 'SELECT MONTH(%s)'
                day_q = 'SELECT DAY(%s)'
                destination_cursor.execute(year_q, (row[3],))
                final_year = destination_cursor.fetchone()[0]  # Fetch the result
                destination_cursor.execute(month_q, (row[3],))
                final_month = destination_cursor.fetchone()[0]  # Fetch the result
                destination_cursor.execute(day_q, (row[3],))
                final_day = destination_cursor.fetchone()[0]
                select_query = "SELECT dateId from dw_dates where dwYear=%s and dwMonth=%s and dwDay=%s limit 1"
                destination_cursor.execute(select_query, (final_year, final_month, final_day))
                date_i = destination_cursor.fetchone()[0]
                if date_i is not None:
                    if source_db == "windsor":
                            data = [(row[0], row[1], row[2], date_i, 1)]
                    elif source_db == "toronto":
                            data = [(row[0], row[1], row[2], date_i, 2)]
                    sql = 'INSERT INTO fact (bid, mid, transType, dateId, branchId) VALUES (%s, %s, %s, %s, %s)'
                    destination_cursor.executemany(sql, data)
                    destination_conn.commit()
            print("Data inserted successfully.")

    except pymysql.Error as e:
        # Rollback the transaction on error
        destination_conn.rollback()
        print("Error inserting data into target table: table_name", e)

    finally:
        # Close cursor and connection
        destination_cursor.close()
        destination_conn.close()


def fill_data_into_branch_table():
    print("Hello")
    data = [(1, 'Windsor Library','123 Main Street, Windsor, ON N9B 2T2', 'John Deere'),
       (2, 'Toronto Library','456 Elm Street, Toronto, ON M5G 2K8', 'Johny Jose')]
    try:
        destination_conn = pymysql.connect(host='localhost', user='root', password='*******', db='library_dw')
        destination_cursor = destination_conn.cursor()
        sql = 'INSERT INTO branch (branchId, bName, bAddress, bManager) VALUES (%s, %s, %s, %s)'
        destination_cursor.executemany(sql, data)
        destination_conn.commit()
    except pymysql.Error as e:
        print("Error inserting date to branch table:", e)
    finally:
        # Close cursor and connection
        destination_cursor.close()
        destination_conn.close()
        
def main():
    fill_data_into_branch_table()
    source_db=("windsor", "toronto")
    for i in source_db:
        insert_date_into_dates_table(i)    
    table_names=("books", "members", "borrowers")
    for i in source_db:
        for j in table_names:
            output=fetch_data_from_source_table(i, j)
            insert_data_into_target_table(output, i, j)
        

if __name__ == "__main__":
    main()







# In[ ]:





# In[ ]:





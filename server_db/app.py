from flask import Flask, request, jsonify, send_file
import sqlite3
import os

app = Flask(__name__)

def create_images_table():
    print(os.getcwd())
    conn = sqlite3.connect('image_database.db')
    c = conn.cursor()
    c.execute('''
        CREATE TABLE IF NOT EXISTS images (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            path TEXT,
            classification TEXT
        )
    ''')
    conn.commit()
    conn.close()

def store_image_path(image_path, classification):
    conn = sqlite3.connect('image_database.db')
    c = conn.cursor()
    c.execute('INSERT INTO images (path, classification) VALUES (?, ?)', (image_path, classification))
    conn.commit()
    conn.close()

def get_image_paths():
    conn = sqlite3.connect('image_database.db')
    c = conn.cursor()
    c.execute('SELECT id, path, classification FROM images')
    rows = c.fetchall()
    conn.close()
    #return [{"id": str(row[0]), "img_src": row[1]} for row in rows]
    return [{"id":f"{row[0]}" , "img_src": f"http://192.168.1.165:5000/get_image/{row[0]}", "classification" : f"{row[2]}"} for row in rows]

@app.route('/store_image', methods=['POST'])
def store_image():
    data = request.get_json()
    image_path = data['image_path']
    classification = data['classification']
    store_image_path(image_path, classification)
    return jsonify(success=True)

@app.route('/get_image/<int:image_id>', methods=['GET'])
def get_image(image_id):
    conn = sqlite3.connect('image_database.db')
    c = conn.cursor()
    c.execute('SELECT path FROM images WHERE id = ?', (image_id,))
    image_path = c.fetchone()
    conn.close()

    if image_path:
        return send_file(image_path[0], mimetype='image/jpeg')
    else:
        return jsonify(error="Image not found"), 404

@app.route('/get_image_paths', methods=['GET'])
def get_images():
    image_paths = get_image_paths()
    return jsonify(image_paths)

@app.route('/delete_image/<int:image_id>', methods=['DELETE'])
def delete_image(image_id):
    conn = sqlite3.connect('image_database.db')
    c = conn.cursor()
    
    # Check if the image exists
    c.execute('SELECT path FROM images WHERE id = ?', (image_id,))
    image_path = c.fetchone()
    
    if not image_path:
        conn.close()
        return jsonify(error="Image not found"), 404
    
    # Delete the image from the database
    c.execute('DELETE FROM images WHERE id = ?', (image_id,))
    
    # Commit the changes and close the connection
    conn.commit()
    conn.close()
    
    # Delete the image file from the filesystem
    try:
        os.remove(image_path[0])
    except OSError as e:
        return jsonify(error="Failed to delete image file"), 500
    
    return jsonify(success=True)

if __name__ == '__main__':
    create_images_table()
    app.run(host='0.0.0.0', port=5000)

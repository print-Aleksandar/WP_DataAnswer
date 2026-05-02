CREATE TABLE client(
    id Serial PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    CONSTRAINT email_format CHECK (email LIKE '%@%.%')
);

CREATE TABLE uploaded_file(
    id Serial PRIMARY KEY,
    client_id INTEGER NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    process_type  VARCHAR(20) NOT NULL, -- 'rag' or 'structured'
    minio_key     VARCHAR(500) NOT NULL, -- minIO key for file storage
    uploaded_at TIMESTAMP DEFAULT now(),
    FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE chat(
    id Serial PRIMARY KEY,
    client_id INTEGER NOT NULL,
    file_id INTEGER NOT NULL,
    chat_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT now(),
    updated_at   TIMESTAMP DEFAULT now(),
    FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (file_id) REFERENCES uploaded_file(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE message(
    id Serial PRIMARY KEY,
    chat_id INTEGER NOT NULL,
    sequence_no INT NOT NULL, 
    question TEXT NOT NULL, 
    answer TEXT NOT NULL, 
    created_at TIMESTAMP DEFAULT now(),
    FOREIGN KEY (chat_id) REFERENCES chat(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_question CHECK (LENGTH(question) > 0),
    CONSTRAINT chk_answer CHECK (LENGTH(answer) > 0)
);

CREATE INDEX idx_chat_client_id ON chat(client_id);
CREATE INDEX idx_message_chat_id ON message(chat_id);
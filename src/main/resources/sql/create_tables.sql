CREATE TABLE IF NOT EXISTS proteins (
    protein_name TEXT PRIMARY KEY,
    light_chain TEXT,
    heavy_chain TEXT,
    CDR_L1 TEXT,
    CDR_L2 TEXT,
    CDR_L3 TEXT,
    CDR_H1 TEXT,
    CDR_H2 TEXT,
    CDR_H3 TEXT);
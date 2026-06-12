from fastapi import File, UploadFile
import fitz
import docx
import odf
from odf import text as odf_text, teletype
from odf.opendocument import load as odf_load
import io
import docx2txt
import tempfile
import os


def __pdf_parse(file_content:bytes):
    # getting the text from the document
    # file_content = await file.read()

    text = ""
    with fitz.open(stream=file_content, filetype="pdf") as pdf:
        for page in pdf:
            text += page.get_text()

    if not text.strip():
        raise ValueError("Could not extract text from PDF.")
    
    return text.strip()


def __docx_parse(file_content: bytes):
    doc = docx.Document(io.BytesIO(file_content))
    text = "\n".join(para.text for para in doc.paragraphs)

    if not text.strip():
        raise ValueError("Could not extract text from DOCX.")
    return text.strip()

def __odt_parse(file_content: bytes):

    doc = odf_load(io.BytesIO(file_content))
    text = teletype.extractText(doc.text)

    if not text.strip():
        raise ValueError("Could not extract text from ODT.")
    return text.strip()

def __txt_parse(file_content: bytes):
    try:
        text = file_content.decode("utf-8")
    except UnicodeDecodeError:
        text = file_content.decode("latin-1")

    if not text.strip():
        raise ValueError("Text file appears to be empty.")
    return text.strip()

def __doc_parse(file_content: bytes):
    """
    docx2txt works on .docx natively; for .doc it needs a temp file.
    Pure Python but limited — may miss complex formatting.
    """
    with tempfile.NamedTemporaryFile(suffix=".doc", delete=False) as tmp:
        tmp.write(file_content)
        tmp_path = tmp.name

    try:
        text = docx2txt.process(tmp_path)
    finally:
        os.unlink(tmp_path)

    if not text.strip():
        raise ValueError("Could not extract text from DOC.")
    return text.strip()

FILETYPE_MAPPER = {
    'pdf': __pdf_parse,
    "docx": __docx_parse,
    "odt":  __odt_parse,
    "txt":  __txt_parse,
    "md":   __txt_parse,
    "doc":  __doc_parse,
}

def get_supported_types():
    return list(FILETYPE_MAPPER.keys())

def parse_file(file:UploadFile, file_content:bytes):
    extention = file.filename.split('.')[-1]
    parse_fn = FILETYPE_MAPPER.get(extention)

    # check if filetype is supported
    if parse_fn is None: raise TypeError(f"Unsuported filetype: '{extention}'" )

    return parse_fn(file_content)


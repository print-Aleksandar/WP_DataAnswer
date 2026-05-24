from fastapi import File, UploadFile
import fitz


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


FILETYPE_MAPPER = {
    'pdf': __pdf_parse
}

def get_supported_types():
    return list(FILETYPE_MAPPER.keys())

def parse_file(file:UploadFile, file_content:bytes):
    extention = file.filename.split('.')[-1]
    parse_fn = FILETYPE_MAPPER.get(extention)

    # check if filetype is supported
    if parse_fn is None: raise TypeError(f"Unsuported filetype: '{extention}'" )

    return parse_fn(file_content)


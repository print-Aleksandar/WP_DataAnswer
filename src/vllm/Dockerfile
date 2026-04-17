FROM python:3.14

# postavuvanje direktorium vo koj raboti aplikacijata
WORKDIR /app

# sprechuvanje baferiranje i/o
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# instalacija na potrebni moduli
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# kopiranje na kodot
COPY . .

# zapochnuvanje na servis
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "9999"]
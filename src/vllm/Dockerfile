FROM python:3.11-slim

# postavuvanje direktorium vo koj raboti aplikacijata
WORKDIR /app

# sprechuvanje baferiranje i/o
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

RUN apt-get update && apt-get install -y \
    libpq-dev \
    gcc \
    && rm -rf /var/lib/apt/lists/*

# instalacija na potrebni moduli
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# kopiranje na kodot
COPY . .

# zapochnuvanje na servis
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "9999"]
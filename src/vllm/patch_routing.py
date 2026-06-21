import pathlib

f = pathlib.Path('/usr/local/lib/python3.12/site-packages/prometheus_fastapi_instrumentator/routing.py')
txt = f.read_text()
txt = txt.replace(
    'route_name = route.path',
    'route_name = getattr(route, "path", None) or ""'
)
f.write_text(txt)
print('patched')
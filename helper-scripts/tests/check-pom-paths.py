"""Check publishing metadata paths without signing or publishing anything."""
from pathlib import Path
import re
variables = (Path(__file__).parents[1] / "project-impl/variables.sh").read_text()
paths = dict(re.findall(r'^(lib_pomFile|ext_pomFile)="([^"]+)"', variables, re.M))
assert paths["lib_pomFile"] != paths["ext_pomFile"], "Core and extension POMs must not overwrite each other"
assert Path(paths["lib_pomFile"]).name == "jector.pom"
assert Path(paths["ext_pomFile"]).name == "jector-monkey.pom"
print("PASS: distinct core and extension POM paths")

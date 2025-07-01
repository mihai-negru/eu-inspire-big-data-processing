import uuid

GENERATE_GROUP_ID_MSG = lambda: '{"command":"generate-group"}'

MERGE_GROUP_ID_MSG = lambda g: f'{{"command":"merge","groupId":"{g}"}}'

SEND_TO_RAW_MSG_BODY = f"<?xml version='1.0' encoding='UTF-8'?><FeatureCollection xmlns='http://www.opengis.net/wfs/2.0' xmlns:gml='http://www.opengis.net/gml/3.2' xmlns:xlink='http://www.w3.org/1999/xlink.xsd' xmlns:gmd='https://www.isotc211.org/2005/gmd/gmd.xsd' xmlns:gn='http://inspire.ec.europa.eu/schemas/gn/4.0/GeographicalNames.xsd' xmlns:au='http://inspire.ec.europa.eu/schemas/2024.2/au/4.0/AdministrativeUnits.xsd' xmlns:base='http://inspire.ec.europa.eu/schemas/base/3.3/BaseTypes.xsd' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><member><gn:GeographicalName><gn:spelling><gn:SpellingOfName><gn:text>{uuid.uuid4()}</gn:text><gn:script>{uuid.uuid4()}</gn:script><gn:transliterationScheme>http://www.translatation/schema/{uuid.uuid4()}</gn:transliterationScheme></gn:SpellingOfName></gn:spelling><gn:language>en</gn:language><gn:pronunciation><gn:PronunciationOfName><gn:pronunciationIPA>{uuid.uuid4()}</gn:pronunciationIPA><gn:pronunciationSoundLink>http://link.pronuntioation.com/{uuid.uuid4()}</gn:pronunciationSoundLink></gn:PronunciationOfName></gn:pronunciation></gn:GeographicalName></member></FeatureCollection>"
SEND_TO_RAW_MSG = lambda g: f'{{"schema":"FCGeographicalName","schemaPath": "name[0].voidValue","groupId":"{g}","message":"{SEND_TO_RAW_MSG_BODY}"}}'
SEND_TO_RAW_MSG_WITH_PATH = lambda g, p: f'{{"schema":"FCGeographicalName","schemaPath": "{p}","groupId":"{g}","message":"{SEND_TO_RAW_MSG_BODY}"}}'

SEND_TO_RAW_ROOT_MSG_BODY = "<?xml version='1.0' encoding='UTF-8' standalone='no'?><FeatureCollection xmlns='http://www.opengis.net/wfs/2.0' xmlns:au='http://inspire.ec.europa.eu/schemas/2024.2/au/4.0/AdministrativeUnits.xsd' xmlns:base='http://inspire.ec.europa.eu/schemas/base/3.3/BaseTypes.xsd' xmlns:gmd='https://www.isotc211.org/2005/gmd/gmd.xsd' xmlns:gml='http://www.opengis.net/gml/3.2' xmlns:gn='http://inspire.ec.europa.eu/schemas/gn/4.0/GeographicalNames.xsd' xmlns:xlink='http://www.w3.org/1999/xlink.xsd' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' numberMatched=''><member><au:Condominium><au:inspireId><base:Identifier><base:localId>OK</base:localId><base:namespace>OK</base:namespace><base:versionId>OK</base:versionId></base:Identifier></au:inspireId></au:Condominium></member></FeatureCollection>"
SEND_TO_RAW_ROOT_MSG = lambda g: f'{{"schema":"FCCondominium","schemaPath":"","groupId":"{g}","message":"{SEND_TO_RAW_ROOT_MSG_BODY}"}}'

import json
import base64

def extract_group_id(message_str):
    try:
        # Step 2: parse JSON
        data = json.loads(message_str)
        # Step 3: extract groupId
        return data.get("groupId")
    except Exception as e:
        print(f"Error extracting groupId: {e}")
        return None

def extract_merged_data_set(message_str):
    try:
        data = json.loads(message_str)

        obj = data.get("obj")
        if obj is None:
            return None

        return base64.b64decode(obj).decode("utf-8")
    except Exception as e:
        return str(e)

def create_raw_content(schema, path, group_id, content):
    return json.dumps({
        "schema": schema,
        "schemaPath": path,
        "groupId": group_id,
        "message": content,
    }).encode("utf-8")

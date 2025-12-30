import requests
import json
import sys
from http.cookies import SimpleCookie
import time
import os
import logging

logging.basicConfig(level=logging.DEBUG)

ENV_STACK_MAPPING = {
    'java21_0__maven': '58',
    'java21_0__gradle': '59',
    'java21_0__springboot': '60',
    'kotlin2_1_android13114758': '79',
    'node_22_mongo_8__mern': '78',
    'node_22_mongo_8__mean': '77',
    'java21_0_android13114758': '76',
    'cpp14': '75',
    'java21_0__cucumber': '74',
    'ruby3_4__rails': '73',
    'spark3_5': '72',
    'pyspark3_5': '71',
    'python3_12__django': '70',
    'php8_4__symfony': '69',
    'php8_4__laravel': '68',
    'php8_4__codeigniter': '67',
    'php8_4': '66',
    'dotnet8_0': '65',
    'nodejs22_16__vuejs': '64',
    'nodejs22_16': '63',
    'nodejs22_16__angularjs': '62',
    'nodejs22_16__reactjs': '61',
    'go1_23': '52',
    'jam__java_21__mongo8': '80',
    'dam__python3_12__mongo8': '81',
    'expo__node_22__react_native': '82',
    'flutter_web__node22': '83',
}

STACK_ENV_MAPPING = { v: k for k, v in ENV_STACK_MAPPING.items() }

STACKS = json.loads(os.environ['HACKERRANK_STACKS'])
TARGET_STACK = os.environ['TARGET_STACK']
TARGET_STACKS = set(STACKS.values())
SOLUTION_VALIDATION = False

SESSION = requests.session()
SESSION.headers.update({
    "Authorization": f"Bearer {os.environ['HACKERRANK_TOKEN']}",
    "cache-control": "no-cache",
    "content-type": "application/json",
})

BASE_URL = "https://www.hackerrank.com/"


def question_exists(qid):
    print('finding question in company library...')

    tag_to_search = f"{qid}-{TARGET_STACK}-solution" if SOLUTION_VALIDATION else f"{qid}-{TARGET_STACK}"

    library_url = BASE_URL + f"x/api/v1/library?limit=1&library=personal_all&tags={tag_to_search},duplicate"
    response = SESSION.request("GET", library_url)

    if response.status_code != 200:
        raise Exception("Failed to find question")

    data = response.json()

    if len(data['model']['questions']) != 0:
        return True, data['model']['questions'][0]

    return False, None


def clone_question(qid):
    print('cloning question...')

    clone_url = BASE_URL + "x/api/v1/questions/clone"
    tag_to_search = f"{qid}-{TARGET_STACK}-solution" if SOLUTION_VALIDATION else f"{qid}-{TARGET_STACK}"
    payload = json.dumps({
        "id": qid,
        "name": f"Copy of {tag_to_search}"
    })
    response = SESSION.request("POST", clone_url, data=payload)

    if response.status_code != 200:
        raise Exception("Failed to clone question")

    data = response.json()

    return data['question']


def get_target_subtype(question):
    if TARGET_STACK != 'based_on_current_stack':
        return TARGET_STACK

    environment_id = question['environment_id']
    sub_type = question['sub_type']
    if environment_id:
        sub_type = STACK_ENV_MAPPING.get(str(environment_id))

    if not STACKS.get(sub_type):
        raise Exception("Target sub type not found")

    return STACKS[sub_type]


def update_question(question, qid):
    target_sub_type = get_target_subtype(question)

    print(f"update question...{question['id']}..cloned..{target_sub_type}")

    original_tags = question['visible_tags_array']
    tag_to_add = f"{qid}-{TARGET_STACK}-solution" if SOLUTION_VALIDATION else f"{qid}-{TARGET_STACK}"

    if question.get("sub_type") == "custom_vm" and ".NET" in original_tags:
        question["sub_type"] = "dotnet3"

    if tag_to_add not in original_tags:
        original_tags.extend([tag_to_add, "duplicate"])


    update_url = BASE_URL + f"x/api/v1/questions/{question['id']}"

    payload = {
        "visible_tags_array": original_tags,
    }
    if target_sub_type in ENV_STACK_MAPPING:
        payload['environment_id'] = int(ENV_STACK_MAPPING[target_sub_type])
        payload['sub_type'] = None
    else:
        payload['sub_type'] = target_sub_type
        payload['environment_id'] = None

    print(f"payload {payload}")
    response = SESSION.request("PUT", update_url, data=json.dumps(payload))

    if not question_updated(response, payload):
        print("Cloned question still have old subtype, retrying")
        response = SESSION.request("PUT", update_url, data=json.dumps(payload))

        if not question_updated(response, payload):
            raise Exception("Cloned question still have old subtype")


def question_updated(response, payload):
    if response.status_code != 200:
        raise Exception("Failed to update cloned question")

    data = response.json()

    sub_type_environment_id = data['model'].get('environment_id', None) or data['model'].get('sub_type', None)
    print(f"Cloned question subtype {sub_type_environment_id}")

    if payload['sub_type'] is not None:
        return sub_type_environment_id in TARGET_STACKS
    elif payload['environment_id'] is not None:
        return str(sub_type_environment_id) in STACK_ENV_MAPPING
    else:
        raise Exception("No sub type or environment id found")


def update_project_zip(question):
    print('updating project zip...')
    update_url = BASE_URL + f"x/api/v1/questions/{question['id']}/upload"
    files = [
        ('source_file', ('project.zip', open('project.zip', 'rb'), 'application/zip'))
    ]
    del SESSION.headers['content-type']
    response = SESSION.post(update_url, files=files, data={'a': 1})

    if response.status_code != 200:
        print(response.status_code)
        raise Exception("Failed to update project zip")

    SESSION.headers.update({'content-type': 'application/json'})


def validate_question(question):
    print('validating question...')
    validate_url = BASE_URL + f"x/api/v1/questions/{question['id']}/validate_fullstack"

    response = SESSION.post(validate_url)

    if response.status_code != 200:
        print('Error in validate_question', response.status_code, response.text)
        raise Exception("Failed to start validation...")

    return response.json()['task_id']


def check_validation_status(task_id):
    print('polling on validation task with id', task_id)
    poll_url = BASE_URL + f"x/api/v1/delayed_tasks/{task_id}/poll"

    while True:
        time.sleep(5)
        response = SESSION.get(poll_url)

        if response.status_code != 200:
            raise Exception("Failed to get validation status", response.status_code, response.text)

        data = response.json()
        if data['status_code'] == 2 or data['response']['additional_data']['valid'] == True:
            print(data)
            return data


def process_validator_response(validator_response):
    print('processing validator response...')
    if not validator_response['valid']:
        for step, value in validator_response['data'].items():
            if value['valid'] == False:
                print(step, value)
                raise Exception(value['error'])

    print("validated successfully")

    scoring_output = validator_response['data']['validate_scoring_output']

    if SOLUTION_VALIDATION and not scoring_output['details']:
        raise Exception('scoring output details is empty')

    if scoring_output['details']:
        score = scoring_output['details']['scoring_data']['score']
        print("scoring output is")
        print(scoring_output)
        print(f"score is {score}")
        if SOLUTION_VALIDATION and score != 1:
            raise Exception('Did not get full score')
        if not SOLUTION_VALIDATION and score != 0:
            raise Exception('Got full or partial score in question')


def main(solution=False):
    global SOLUTION_VALIDATION
    global SESSION

    try:
        # id of original question from library
        qid = sys.argv[1].split('-')[0]
        print(f"detected qid is {qid}")

        if not qid.isdigit():
            raise Exception('Could not parse Question ID')

        # solution validation from args
        branch_name = sys.argv[2] if len(sys.argv) == 3 else ""
        print(f"detected branch name is {branch_name}")

        SOLUTION_VALIDATION = solution or "solution" in branch_name
        if SOLUTION_VALIDATION:
            print(f"Doing *****SOLUTION***** Validation")
            SESSION.headers.update({
                "Authorization": f"Bearer {os.environ['SOLUTION_TOKEN']}"
            })

        # get clone of question if exists
        exists, question = question_exists(qid)

        if not exists:
            question = clone_question(qid)
        print('question copy has id', question['id'])
        update_question(question, qid)
        update_project_zip(question)
        task_id = validate_question(question)
        validator_response = check_validation_status(task_id)
        process_validator_response(validator_response['response'])
    except Exception as e:
        print(e)
        os._exit(1)


if __name__ == "__main__":
    main()

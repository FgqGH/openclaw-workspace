#!/usr/bin/env python3
"""GCP 完整部署 v5"""

import json, base64, time, requests, hashlib

creds = json.load(open('/root/.openclaw/workspace/full-stack-developer/gcp-credentials.json'))
PROJECT = "my-project-openclaw-492614"
REGION = "asia-east1"
DB_INSTANCE = "campus-sports-mysql"
DB_NAME = "campus_sports"
DB_USER = "campus_sports_user"
DB_PASSWORD = "CampusSports2024!"
REPO = "campus-sports-repo"
BUCKET = f"{PROJECT}-csb"
BACKEND = "/root/.openclaw/workspace/full-stack-developer/campus-sports-booking/backend"
SQL_FILE = "/root/.openclaw/workspace/full-stack-developer/campus-sports-booking/scripts/campus-sports-init.sql"

def get_token():
    now = int(time.time())
    header_b64 = base64.urlsafe_b64encode(json.dumps({'alg': 'RS256', 'typ': 'JWT'}).encode()).decode().rstrip('=')
    payload_b64 = base64.urlsafe_b64encode(json.dumps({
        'iss': creds['client_email'], 'sub': creds['client_email'],
        'aud': 'https://oauth2.googleapis.com/token',
        'iat': now, 'exp': now + 3600,
        'scope': 'https://www.googleapis.com/auth/cloud-platform'
    }).encode()).decode().rstrip('=')
    pk = __import__('cryptography.hazmat.primitives.serialization').hazmat.primitives.serialization.load_pem_private_key(
        creds['private_key'].encode(), password=None,
        backend=__import__('cryptography.hazmat.backends').hazmat.backends.default_backend())
    sig = pk.sign(f'{header_b64}.{payload_b64}'.encode(),
        __import__('cryptography.hazmat.primitives.asymmetric.padding').hazmat.primitives.asymmetric.padding.PKCS1v15(),
        __import__('cryptography.hazmat.primitives.hashes').hazmat.primitives.hashes.SHA256())
    jwt = f'{header_b64}.{payload_b64}.' + base64.urlsafe_b64encode(sig).decode().rstrip('=')
    return requests.post('https://oauth2.googleapis.com/token',
        data={'grant_type': 'urn:ietf:params:oauth:grant-type:jwt-bearer', 'assertion': jwt}, timeout=30).json()['access_token']

def api(method, url, token, **kw):
    r = requests.request(method, url, headers={"Authorization": f"Bearer {token}"}, timeout=60, **kw)
    return r

def wait_operation(op_url, token, name="操作"):
    for i in range(20):
        time.sleep(3)
        r = api("GET", f"https://artifactregistry.googleapis.com/v1/{op_url}", token)
        done = r.json().get("done", False)
        if done:
            print(f"   ✅ {name}完成")
            return True
        print(f"   ⏳ {name}第{i+1}次检查...")
    print(f"   ⚠️  {name}超时")
    return False

token = get_token()
print("✅ 认证成功\n")

# ===== 1. 确保 Artifact Registry 仓库存在 =====
print("📋 1. Artifact Registry...")
r = api("GET", f"https://artifactregistry.googleapis.com/v1/projects/{PROJECT}/locations/{REGION}/repositories/{REPO}", token)
if r.status_code == 200:
    print("   ✅ 仓库已存在")
else:
    r = api("POST",
        f"https://artifactregistry.googleapis.com/v1/projects/{PROJECT}/locations/{REGION}/repositories?repository_id={REPO}",
        token, json={"format": "DOCKER", "description": "Campus Sports"})
    if r.status_code == 200:
        wait_operation(r.json().get("name", ""), token, "AR仓库创建")
    else:
        print(f"   ⚠️  {r.status_code}: {r.text[:100]}")

# ===== 2. 创建 GCS Bucket 并上传 SQL =====
print("\n📋 2. GCS Bucket...")
r = api("GET", f"https://storage.googleapis.com/storage/v1/b?project={PROJECT}", token)
existing = [b["name"] for b in r.json().get("items", [])]
if BUCKET in existing:
    print("   ✅ Bucket 已存在")
else:
    r = requests.post(
        f"https://storage.googleapis.com/storage/v1/b?project={PROJECT}",
        headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json"},
        json={"name": BUCKET, "location": REGION}, timeout=30)
    print(f"   {'✅' if r.status_code in [200,201] else '⚠️'} Bucket: {r.status_code}")

print("   上传 SQL 文件...")
with open(SQL_FILE, "rb") as f:
    sql_content = f.read()
r = requests.post(
    f"https://storage.googleapis.com/upload/storage/v1/b/{BUCKET}/o?uploadType=media&name=campus-sports-init.sql",
    headers={"Authorization": f"Bearer {token}", "Content-Type": "application/x-sql"},
    data=sql_content, timeout=30)
print(f"   {'✅' if r.status_code == 200 else '⚠️'} SQL上传: {r.status_code}")

# ===== 3. 导入 SQL 到 Cloud SQL =====
print("\n📋 3. 导入 SQL 到 Cloud SQL...")
r = api("POST", f"https://sqladmin.googleapis.com/v1/projects/{PROJECT}/instances/{DB_INSTANCE}/imports", token,
    json={"importContext": {"fileType": "SQL", "uri": f"gs://{BUCKET}/campus-sports-init.sql", "database": DB_NAME}})
print(f"   {'✅' if r.status_code in [200,201] else '⚠️'} 导入请求: {r.status_code}")
print("   ⏳ 等待导入完成（90秒）...")
time.sleep(90)

# ===== 4. Cloud Build 构建并推送 =====
print("\n📋 4. Cloud Build...")
commit = hashlib.sha1(str(time.time()).encode()).hexdigest()[:12]
r = api("POST", f"https://cloudbuild.googleapis.com/v1/projects/{PROJECT}/builds", token, json={
    "steps": [{"name": "gcr.io/cloud-builders/docker",
               "args": ["build", "--push",
                        "-t", f"asia-east1-docker.pkg.dev/{PROJECT}/{REPO}/backend:{commit}",
                        "-t", f"asia-east1-docker.pkg.dev/{PROJECT}/{REPO}/backend:latest",
                        "-f", "Dockerfile", "."]}],
    "images": [f"asia-east1-docker.pkg.dev/{PROJECT}/{REPO}/backend:{commit}",
               f"asia-east1-docker.p.dev/{PROJECT}/{REPO}/backend:latest"],
    "timeout": "600s"
})
print(f"   Cloud Build 响应: {r.status_code}")
if r.status_code not in [200, 201]:
    print(f"   ❌ 失败: {r.text[:300]}")
else:
    data = r.json()
    op_name = data.get("name", "")
    print(f"   ✅ Build提交: {op_name}")
    for i in range(40):
        time.sleep(20)
        r = api("GET", f"https://cloudbuild.googleapis.com/v1/{op_name}", token)
        resp = r.json()
        done = resp.get("done", False)
        status = resp.get("metadata", {}).get("status", resp.get("status", ""))
        print(f"     第{i+1}次: {status} (done={done})")
        if done:
            success = bool(resp.get("response"))
            print(f"   {'✅ 构建成功!' if success else '❌ 构建失败'}")
            break
    else:
        print("   ⚠️  等待超时")

# ===== 5. 部署到 Cloud Run =====
print("\n📋 5. Cloud Run...")
r = api("GET", f"https://sqladmin.googleapis.com/v1/projects/{PROJECT}/instances/{DB_INSTANCE}", token)
conn_name = r.json()["connectionName"]
image = f"asia-east1-docker.pkg.dev/{PROJECT}/{REPO}/backend:{commit}"

body = {
    "template": {
        "containers": [{"image": image, "ports": [{"containerPort": 8080}],
                        "env": [{"name": "DB_HOST", "value": f"/cloudsql/{conn_name}"},
                                {"name": "DB_PORT", "value": "3306"},
                                {"name": "DB_NAME", "value": DB_NAME},
                                {"name": "DB_USERNAME", "value": DB_USER},
                                {"name": "DB_PASSWORD", "value": DB_PASSWORD},
                                {"name": "JWT_SECRET", "value": "campus-sports-gcp-jwt-prod-2024"}],
                        "resources": {"limits": {"memory": "512Mi", "cpu": "1"}}}],
        "scaling": {"minInstanceCount": 0, "maxInstanceCount": 10}
    }
}

url = f"https://run.googleapis.com/v1/projects/{PROJECT}/locations/{REGION}/services/campus-sports-backend"
r = api("GET", url, token)

if r.status_code == 200:
    r = api("PATCH", url, token, json=body)
    print(f"   {'✅' if r.status_code in [200, 201] else '❌'} 更新服务: {r.status_code}")
else:
    full_body = {"apiVersion": "serving.knative.dev/v1", "kind": "Service",
                  "metadata": {"name": "campus-sports-backend"}, **body}
    r = api("POST", f"https://run.googleapis.com/v1/projects/{PROJECT}/locations/{REGION}/services", token, json=full_body)
    print(f"   {'✅' if r.status_code in [200, 201] else '❌'} 创建服务: {r.status_code}")
    if r.status_code not in [200, 201]:
        print(f"   错误: {r.text[:200]}")

time.sleep(15)

r = api("GET", url, token)
if r.status_code == 200:
    svc_url = r.json().get("uri", "")
    condition = r.json().get("conditions", [{}])[0].get("message", "")
else:
    svc_url = "未知"
    condition = ""
    print(f"   ⚠️  获取URL失败: {r.status_code} {r.text[:100]}")

print(f"\n{'='*50}")
print(f"🎉 后端部署{'成功!' if svc_url != '未知' else '完成(请检查状态)'}")
print(f"   后端: {svc_url}")
print(f"   API:  {svc_url}/api")
print(f"{'='*50}")
if svc_url != "未知":
    print(f"\n📝 下一步:")
    print(f"  前端 .env.production:")
    print(f"    VITE_API_BASE_URL={svc_url}/api")

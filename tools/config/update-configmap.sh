envValue=$1
APP_NAME=$2
OPENSHIFT_NAMESPACE=$3
DB_JDBC_CONNECT_STRING=$5
DB_PWD=$6
DB_USER=$7
SPLUNK_TOKEN=$8
BRANCH=$9

TZVALUE="America/Vancouver"
SOAM_KC_REALM_ID="master"
SOAM_KC=soam-$envValue.apps.silver.devops.gov.bc.ca

SOAM_KC_LOAD_USER_ADMIN=$(oc -n "$OPENSHIFT_NAMESPACE"-"$envValue" -o json get secret sso-admin-"${envValue}" | sed -n 's/.*"username": "\(.*\)"/\1/p' | base64 --decode)
SOAM_KC_LOAD_USER_PASS=$(oc -n "$OPENSHIFT_NAMESPACE"-"$envValue" -o json get secret sso-admin-"${envValue}" | sed -n 's/.*"password": "\(.*\)",/\1/p' | base64 --decode)

NATS_CLUSTER=educ_nats_cluster
NATS_URL="nats://nats.${OPENSHIFT_NAMESPACE}-${envValue}.svc.cluster.local:4222"

echo Fetching SOAM token
TKN=$(curl -s \
  -d "client_id=admin-cli" \
  -d "username=$SOAM_KC_LOAD_USER_ADMIN" \
  -d "password=$SOAM_KC_LOAD_USER_PASS" \
  -d "grant_type=password" \
  "https://$SOAM_KC/auth/realms/$SOAM_KC_REALM_ID/protocol/openid-connect/token" | jq -r '.access_token')

###########################################################
#Setup for scopes
###########################################################
echo
echo Writing scope READ_STUDENT_PROFILE
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Read scope for Student Profile\",\"id\": \"READ_STUDENT_PROFILE\",\"name\": \"READ_STUDENT_PROFILE\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope WRITE_STUDENT_PROFILE
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Write scope for Student Profile\",\"id\": \"WRITE_STUDENT_PROFILE\",\"name\": \"WRITE_STUDENT_PROFILE\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope DELETE_DOCUMENT_STUDENT_PROFILE
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Delete scope for Student Profile Document\",\"id\": \"DELETE_DOCUMENT_STUDENT_PROFILE\",\"name\": \"DELETE_DOCUMENT_STUDENT_PROFILE\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_DOCUMENT_STUDENT_PROFILE
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Read scope for Student Profile Document\",\"id\": \"READ_DOCUMENT_STUDENT_PROFILE\",\"name\": \"READ_DOCUMENT_STUDENT_PROFILE\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_DOCUMENT_REQUIREMENTS_STUDENT_PROFILE
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Read scope for Student Profile Document Requirements\",\"id\": \"READ_DOCUMENT_REQ_STUDENT_PROFILE\",\"name\": \"READ_DOCUMENT_REQUIREMENTS_STUDENT_PROFILE\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope WRITE_DOCUMENT_STUDENT_PROFILE
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Write scope for Student Profile Document\",\"id\": \"WRITE_DOCUMENT_STUDENT_PROFILE\",\"name\": \"WRITE_DOCUMENT_STUDENT_PROFILE\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_DOCUMENT_TYPES_STUDENT_PROFILE
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Read scope for Student Profile Document Types\",\"id\": \"READ_DOCUMENT_TYPES_STUDENT_PROFILE\",\"name\": \"READ_DOCUMENT_TYPES_STUDENT_PROFILE\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_STUDENT_PROFILE_STATUSES
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Read scope for Student Profile Statuses\",\"id\": \"READ_STUDENT_PROFILE_STATUSES\",\"name\": \"READ_STUDENT_PROFILE_STATUSES\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_STUDENT_PROFILE_CODES
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Read scope for Student Profile Codes\",\"id\": \"READ_STUDENT_PROFILE_CODES\",\"name\": \"READ_STUDENT_PROFILE_CODES\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_STUDENT_PROFILE_MACRO
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"SOAM read pen request macro scope\",\"id\": \"READ_STUDENT_PROFILE_MACRO\",\"name\": \"READ_STUDENT_PROFILE_MACRO\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope WRITE_STUDENT_PROFILE_MACRO
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"SOAM write pen request macro scope\",\"id\": \"WRITE_STUDENT_PROFILE_MACRO\",\"name\": \"WRITE_STUDENT_PROFILE_MACRO\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
echo
echo Writing scope READ_STUDENT_PROFILE_STATS
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Scope to query stats for reporting\",\"id\": \"READ_STUDENT_PROFILE_STATS\",\"name\": \"READ_STUDENT_PROFILE_STATS\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

###########################################################
#Setup for config-map
###########################################################
SPLUNK_URL="gww.splunk.educ.gov.bc.ca"
FLB_CONFIG="[SERVICE]
   Flush        1
   Daemon       Off
   Log_Level    debug
   HTTP_Server   On
   HTTP_Listen   0.0.0.0
   Parsers_File parsers.conf
[INPUT]
   Name   tail
   Path   /mnt/log/*
   Exclude_Path *.gz,*.zip
   Parser docker
   Mem_Buf_Limit 20MB
[FILTER]
   Name record_modifier
   Match *
   Record hostname \${HOSTNAME}
[OUTPUT]
   Name   stdout
   Match  *
[OUTPUT]
   Name  splunk
   Match *
   Host  $SPLUNK_URL
   Port  443
   TLS         On
   TLS.Verify  Off
   Message_Key $APP_NAME
   Splunk_Token $SPLUNK_TOKEN
"
PARSER_CONFIG="
[PARSER]
    Name        docker
    Format      json
"
echo
echo Creating config map "$APP_NAME-config-map"
oc create -n "$OPENSHIFT_NAMESPACE-$envValue" configmap "$APP_NAME-config-map" \
  --from-literal=TZ=$TZVALUE \
  --from-literal=TOKEN_ISSUER_URL="https://$SOAM_KC/auth/realms/$SOAM_KC_REALM_ID" \
  --from-literal=NATS_URL="$NATS_URL" \
  --from-literal=NATS_CLUSTER=$NATS_CLUSTER \
  --from-literal=JDBC_URL="$DB_JDBC_CONNECT_STRING" \
  --from-literal=ORACLE_USERNAME="$DB_USER" \
  --from-literal=ORACLE_PASSWORD="$DB_PWD" \
  --from-literal=SPRING_SECURITY_LOG_LEVEL=INFO \
  --from-literal=SPRING_WEB_LOG_LEVEL=INFO \
  --from-literal=APP_LOG_LEVEL=INFO \
  --from-literal=HIBERNATE_STATISTICS=false \
  --from-literal=SPRING_BOOT_AUTOCONFIG_LOG_LEVEL=INFO \
  --from-literal=SPRING_SHOW_REQUEST_DETAILS=false \
  --from-literal=FILE_EXTENSIONS="image/jpeg,image/png,application/pdf,.jpg,.jpeg,.jpe,.jfif,.jif,.jfi" \
  --from-literal=FILE_MAXSIZE=10485760 \
  --from-literal=FILE_MAX_ENCODED_SIZE=15485760 \
  --from-literal=BCSC_AUTO_MATCH_OUTCOMES="RIGHTPEN,WRONGPEN,ZEROMATCHES,MANYMATCHES,ONEMATCH" \
  --from-literal=REMOVE_BLOB_CONTENTS_DOCUMENT_AFTER_DAYS="365" \
  --from-literal=SCHEDULED_JOBS_REMOVE_BLOB_CONTENTS_DOCUMENT_CRON="@midnight" \
  --from-literal=NATS_MAX_RECONNECT=60 \
  --from-literal=PURGE_RECORDS_EVENT_AFTER_DAYS=365 \
  --from-literal=SCHEDULED_JOBS_PURGE_OLD_EVENT_RECORDS_CRON="@midnight" \
  --dry-run=client -o yaml | oc apply -f -

echo
echo Setting environment variables for "$APP_NAME-$BRANCH" application
oc -n "$OPENSHIFT_NAMESPACE-$envValue" set env \
  --from="configmap/$APP_NAME-config-map" "deployment/$APP_NAME-$BRANCH"

echo Creating config map "$APP_NAME-flb-sc-config-map"
oc create -n "$OPENSHIFT_NAMESPACE-$envValue" configmap \
  "$APP_NAME"-flb-sc-config-map \
  --from-literal=fluent-bit.conf="$FLB_CONFIG" \
  --from-literal=parsers.conf="$PARSER_CONFIG" \
  --dry-run=client -o yaml | oc apply -f -

echo Removing un-needed config entries
oc -n "$OPENSHIFT_NAMESPACE-$envValue" set env \
  "deployment/$APP_NAME-$BRANCH" KEYCLOAK_PUBLIC_KEY-

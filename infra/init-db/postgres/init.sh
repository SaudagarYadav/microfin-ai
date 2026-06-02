#!/bin/bash
# ===========================================================
# FINPILOT - Postgres bootstrap (runs only on first container
# start when the data volume is empty).
#
# - Creates per-service databases.
# - Applies each service's schema against its own database.
# Schema files are plain SQL (no psql meta-commands) so they
# can also be run manually from pgAdmin / DBeaver.
# ===========================================================
set -euo pipefail

PSQL="psql -v ON_ERROR_STOP=1 --username ${POSTGRES_USER}"

create_db_if_missing() {
    local db_name="$1"
    local exists
    exists=$(${PSQL} --dbname "${POSTGRES_DB}" -tAc \
        "SELECT 1 FROM pg_database WHERE datname = '${db_name}'")
    if [ "${exists}" != "1" ]; then
        echo "[finpilot-init] Creating database ${db_name}"
        ${PSQL} --dbname "${POSTGRES_DB}" -c \
            "CREATE DATABASE ${db_name} OWNER ${POSTGRES_USER}"
    else
        echo "[finpilot-init] Database ${db_name} already exists, skipping"
    fi
}

apply_schema() {
    local db_name="$1"
    local schema_file="$2"
    if [ -f "${schema_file}" ]; then
        echo "[finpilot-init] Applying ${schema_file} -> ${db_name}"
        ${PSQL} --dbname "${db_name}" -f "${schema_file}"
    else
        echo "[finpilot-init] Schema file ${schema_file} not found, skipping"
    fi
}

# -----------------------------------------------------------
# auth-service
# -----------------------------------------------------------
create_db_if_missing "microfin_auth"
apply_schema        "microfin_auth" "/finpilot/sql/auth-schema.sql"

echo "[finpilot-init] Bootstrap complete."

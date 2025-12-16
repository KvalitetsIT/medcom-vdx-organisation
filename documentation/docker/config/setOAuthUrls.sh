#! /bin/sh

if [[ -z "${AUTH_URL}" ]]; then
  echo "AUTH_URL NOT set"
else
  for file in $DOC_FILES; do

    yq w -i "$file" 'components.securitySchemes.keycloakOIDC.flows.authorizationCode.authorizationUrl' "$AUTH_URL"
  done
fi

if [[ -z "${TOKEN_URL}" ]]; then
  echo "TOKEN_URL NOT set"
else
  for file in $DOC_FILES; do

    yq w -i "$file" 'components.securitySchemes.keycloakOIDC.flows.authorizationCode.tokenUrl' "$TOKEN_URL"

    yq w -i "$file" 'components.securitySchemes.keycloakOIDC.flows.clientCredentials.tokenUrl' "$TOKEN_URL"

  done
fi



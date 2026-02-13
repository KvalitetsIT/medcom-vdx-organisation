FROM swaggerapi/swagger-ui:v5.22.0 AS swagger

FROM nginxinc/nginx-unprivileged:1.29-alpine

# Kopiér Swagger UI filer
COPY --from=swagger /etc/nginx/templates /etc/nginx/templates
COPY --from=swagger /usr/share/nginx/html /usr/share/nginx/html
COPY --from=swagger /docker-entrypoint.d /docker-entrypoint.d
COPY --from=swagger /usr/share/nginx/configurator /usr/share/nginx/configurator

COPY /maven/*.yaml /usr/share/nginx/html/

COPY /versions /kit/versions
COPY /config/setVersion.sh /kit/setVersion.sh
COPY /config/setServers.sh /kit/setServers.sh
COPY /config/setOAuthUrls.sh /kit/setOAuthUrls.sh
COPY /config/buildDocumentation.sh /kit/buildDocumentation.sh
COPY /config/config.sh /config.sh

COPY /config/nginx.conf /etc/nginx/nginx.conf
COPY /config/proxy.conf.template /etc/nginx/proxy.conf.template

COPY /maven/runningVersion.json /kit/runningVersion.json

USER root
RUN apk update && \
    apk add jq && \
    apk add --no-cache nodejs && \
    wget -O /usr/bin/yq "https://github.com/mikefarah/yq/releases/download/3.3.2/yq_linux_amd64" && \
    chmod +x /usr/bin/yq && \
    chmod +x /kit/setVersion.sh && \
    chmod +x /kit/setServers.sh && \
    chmod +x /kit/setOAuthUrls.sh && \
    chmod +x /kit/buildDocumentation.sh && \
    chmod +x /config.sh && \
    chown -R nginx:nginx /kit /usr/share/nginx

USER nginx

ENTRYPOINT ["sh", "/config.sh"]

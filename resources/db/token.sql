drop table if exists oauth_client_details;
create table oauth_client_details (
                                    client_id VARCHAR(256) PRIMARY KEY,
                                    resource_ids VARCHAR(256),
                                    client_secret VARCHAR(512),
                                    scope VARCHAR(256),
                                    authorized_grant_types VARCHAR(256),
                                    web_server_redirect_uri VARCHAR(256),
                                    authorities VARCHAR(256),
                                    access_token_validity INTEGER,
                                    refresh_token_validity INTEGER,
                                    additional_information VARCHAR(4096),
                                    autoapprove VARCHAR(256)
);

INSERT INTO oauth_client_details
(client_id, client_secret,
scope, authorized_grant_types,
authorities, access_token_validity, refresh_token_validity)
VALUES
('rest_client', '$2a$10$6BMqmq4/AOWzGPWHoO2lXeDYEt.6XnwLI.CO79Yz8n.UnsunGwA86',
'read, write, trust', 'password, refresh_token',
'ROLE_CLIENT, ROLE_TRUSTED_CLIENT', 60, 3600);



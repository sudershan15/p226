This example requires two postgresql servers.

Option 1: two guest VMs

1. Configure a posgresql server in each VM (port 5432)
2. Add the same user/role to each
3. Create the same database in each (hibann)
4. Set the port (url) for hibernate.cfg.xml
5. You will likely want to use build-2.xml (needs to be debugged)

Option 2: two postgresql servers in the same OS

1. Configure a default postgesql server (port 5432) and start
2. Create a second postgresql database (initdb -D /opt/post-db2)
     a. Create directory before running initdb, set owner to postgres
     b. Configure second db's pg_hba.conf and postgresql.conf (port 5433)
3. Start second: ./pg_ctl -D /opt/post-db2 start
4. configure user/role and database (hibann)
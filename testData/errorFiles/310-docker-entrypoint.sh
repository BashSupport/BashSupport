#!/bin/bash
set -eo pipefail

# if command starts with an option, prepend mysqld
if [ "${1:0:1}" = "-" ]; then
    set -- mysqld "$@"
fi

# skip setup if they want an option that stops mysqld
wantHelp=
for arg; do
    case "$arg" in
	-"?"|--help|--print-defaults|-V|--version)
	    wantHelp=1
	break
	;;
    esac
done

# write_conf_value2 $MYSQL_MYSQLD_USER "user" ["mysqld.cnf"]
write_conf() {
    if [ ! -z "$1" ]; then
        cnf="/etc/mysql/conf.d/mysqld.cnf"
        if [ ! -z "$3" ]; then
            cnf="/etc/mysql/conf.d/$3"
        fi
        echo -n " - checking and writing $2"
        echo $2 >> ${cnf}
        echo $(mysqld --verbose --help 1>/dev/null)
    fi
}

# write_conf_value2 MYSQL_MYSQLD_USER "$MYSQL_MYSQLD_USER" "user" "mysql" ["mysqld.cnf"]
write_conf_value() {
    if [ ! -z $2 ]; then
        cnf="/etc/mysql/conf.d/mysqld.cnf"
        if [ ! -z "$3" ]; then
            cnf="/etc/mysql/conf.d/$3"
        fi
        echo -n " - checking and writing $1 = $2"
        echo $1" = "${2} >> ${cnf}
        echo $(mysqld --verbose --help 1>/dev/null)
    fi
}

if [ "$1" = "mysqld" -a -z "$wantHelp" ]; then
    # Get config
    DATADIR="$("$@" --verbose --help 2>/dev/null | awk '$1 == "datadir" { print $2; exit }')"

    if [ ! -f /etc/mysql/conf.d/mysqld.cnf ]; then
	    echo "Creating /etc/mysql/conf.d/mysqld.cnf from environment parameters ..."
	    echo "[mysqld]" > /etc/mysql/conf.d/mysqld.cnf

	    MYSQL_MYSQLD_USER=${MYSQL_MYSQLD_USER:-mysql} && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} /etc/mysql && write_conf_value "user" ${MYSQL_MYSQLD_USER}
        MYSQL_MYSQLD_DATADIR=${MYSQL_MYSQLD_DATADIR:-$DATADIR} && mkdir -p -m 0750 ${MYSQL_MYSQLD_DATADIR} && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} ${MYSQL_MYSQLD_DATADIR} && write_conf_value "datadir" ${MYSQL_MYSQLD_DATADIR}
        MYSQL_MYSQLD_BIND_ADDRESS=${MYSQL_MYSQLD_BIND_ADDRESS:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "bind-address" { print $2; exit }')} && write_conf_value "bind-address" ${MYSQL_MYSQLD_BIND_ADDRESS}

        if [ -n "$REPLICATION_SLAVE" ]; then
            MYSQL_MYSQLD_SERVER_ID=${MYSQL_MYSQLD_SERVER_ID:-2}
        else
            MYSQL_MYSQLD_SERVER_ID=${MYSQL_MYSQLD_SERVER_ID:-1}
        fi
        write_conf_value "server-id" ${MYSQL_MYSQLD_SERVER_ID}
	    MYSQL_MYSQLD_SQL_MODE=${MYSQL_MYSQLD_SQL_MODE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "sql-mode" { print $2; exit }')} && \
	        write_conf_value  "sql-mode" ${MYSQL_MYSQLD_SQL_MODE}
        MYSQL_MYSQLD_BASEDIR=${MYSQL_MYSQLD_BASEDIR:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "basedir" { print $2; exit }')} && \
            write_conf_value "basedir" ${MYSQL_MYSQLD_BASEDIR}
        MYSQL_MYSQLD_INNODB_DATA_HOME_DIR=${MYSQL_MYSQLD_INNODB_DATA_HOME_DIR:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_data_home_dir" { print $2; exit }')} && \
            mkdir -p -m 0750 ${MYSQL_MYSQLD_INNODB_DATA_HOME_DIR} && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} ${MYSQL_MYSQLD_INNODB_DATA_HOME_DIR} && \
            write_conf_value "innodb_data_home_dir" ${MYSQL_MYSQLD_INNODB_DATA_HOME_DIR}
        MYSQL_MYSQLD_INNODB_LOG_GROUP_HOME_DIR=${MYSQL_MYSQLD_INNODB_LOG_GROUP_HOME_DIR:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_log_group_home_dir" { print $2; exit }')} && \
            mkdir -p -m 0750 ${MYSQL_MYSQLD_INNODB_LOG_GROUP_HOME_DIR} && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} ${MYSQL_MYSQLD_INNODB_LOG_GROUP_HOME_DIR} && \
            write_conf_value "innodb_log_group_home_dir" ${MYSQL_MYSQLD_INNODB_LOG_GROUP_HOME_DIR}
        MYSQL_MYSQLD_LOG_BIN=${MYSQL_MYSQLD_LOG_BIN:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "log-bin" { print $2; exit }')} && \
            mkdir -p -m 0750 $(dirname ${MYSQL_MYSQLD_LOG_BIN}) && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} $(dirname ${MYSQL_MYSQLD_LOG_BIN}) && \
            write_conf_value "log-bin" ${MYSQL_MYSQLD_LOG_BIN}
        MYSQL_MYSQLD_LOG_ERROR=${MYSQL_MYSQLD_LOG_ERROR:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "log-error" { print $2; exit }')} && \
            mkdir -p -m 0750 $(dirname ${MYSQL_MYSQLD_LOG_ERROR}) && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} $(dirname ${MYSQL_MYSQLD_LOG_ERROR}) && \
            write_conf_value "log-error" ${MYSQL_MYSQLD_LOG_ERROR}
        MYSQL_MYSQLD_MASTER_INFO_FILE=${MYSQL_MYSQLD_MASTER_INFO_FILE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "master_info_file" { print $2; exit }')} && \
            mkdir -p -m 0750 $(dirname ${MYSQL_MYSQLD_MASTER_INFO_FILE}) && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} $(dirname ${MYSQL_MYSQLD_MASTER_INFO_FILE}) && \
            write_conf_value "master_info_file" ${MYSQL_MYSQLD_MASTER_INFO_FILE}
        MYSQL_MYSQLD_PID_FILE=${MYSQL_MYSQLD_PID_FILE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "pid-file" { print $2; exit }')} && \
            mkdir -p -m 0750 $(dirname ${MYSQL_MYSQLD_PID_FILE}) && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} $(dirname ${MYSQL_MYSQLD_PID_FILE}) && \
            write_conf_value "pid-file" ${MYSQL_MYSQLD_PID_FILE}
        MYSQL_MYSQLD_RELAY_LOG=${MYSQL_MYSQLD_RELAY_LOG:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "relay_log" { print $2; exit }')} && \
            mkdir -p -m 0750 $(dirname ${MYSQL_MYSQLD_RELAY_LOG}) && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} $(dirname ${MYSQL_MYSQLD_RELAY_LOG}) && \
            write_conf_value "relay_log" ${MYSQL_MYSQLD_RELAY_LOG}
        MYSQL_MYSQLD_RELAY_LOG_INFO_FILE=${MYSQL_MYSQLD_RELAY_LOG_INFO_FILE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "relay_log_info_file" { print $2; exit }')} && \
            mkdir -p -m 0750 $(dirname ${MYSQL_MYSQLD_RELAY_LOG_INFO_FILE}) && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} $(dirname ${MYSQL_MYSQLD_RELAY_LOG_INFO_FILE}) && \
            write_conf_value "relay_log_info_file" ${MYSQL_MYSQLD_RELAY_LOG_INFO_FILE}
        MYSQL_MYSQLD_RELAY_LOG_INDEX=${MYSQL_MYSQLD_RELAY_LOG_INDEX:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "relay_log_index" { print $2; exit }')} && \
            mkdir -p -m 0750 $(dirname ${MYSQL_MYSQLD_RELAY_LOG_INDEX}) && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} $(dirname ${MYSQL_MYSQLD_RELAY_LOG_INDEX}) && \
            write_conf_value "relay_log_index" ${MYSQL_MYSQLD_RELAY_LOG_INDEX}
        MYSQL_MYSQLD_SLOW_QUERY_LOG=${MYSQL_MYSQLD_SLOW_QUERY_LOG:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "slow_query_log" { print $2; exit }')} && \
            write_conf_value "slow_query_log" ${MYSQL_MYSQLD_SLOW_QUERY_LOG}
        MYSQL_MYSQLD_SLOW_QUERY_LOG_FILE=${MYSQL_MYSQLD_SLOW_QUERY_LOG_FILE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "slow_query_log_file" { print $2; exit }')} && \
            mkdir -p -m 0750 $(dirname ${MYSQL_MYSQLD_SLOW_QUERY_LOG_FILE}) && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} $(dirname ${MYSQL_MYSQLD_SLOW_QUERY_LOG_FILE}) && \
            write_conf_value "slow_query_log_file" ${MYSQL_MYSQLD_SLOW_QUERY_LOG_FILE}
        MYSQL_MYSQLD_SOCKET=${MYSQL_MYSQLD_SOCKET:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "socket" { print $2; exit }')} && \
            mkdir -p -m 0750 $(dirname ${MYSQL_MYSQLD_SOCKET}) && chown -R ${MYSQL_MYSQLD_USER}:${MYSQL_MYSQLD_USER} $(dirname ${MYSQL_MYSQLD_SOCKET}) && \
            write_conf_value "socket" ${MYSQL_MYSQLD_SOCKET}
        MYSQL_MYSQLD_TMPDIR=${MYSQL_MYSQLD_TMPDIR:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "tmpdir" { print $2; exit }')} && \
            write_conf_value "tmpdir" ${MYSQL_MYSQLD_TMPDIR}
        MYSQL_MYSQLD_LOG_SLAVE_UPDATES=${MYSQL_MYSQLD_LOG_SLAVE_UPDATES:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "log-slave-updates" { print $2; exit }')} && \
            write_conf_value "log-slave-updates" ${MYSQL_MYSQLD_LOG_SLAVE_UPDATES}
        MYSQL_MYSQLD_EXPIRE_LOGS_DAYS=${MYSQL_MYSQLD_EXPIRE_LOGS_DAYS:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "expire_logs_days" { print $2; exit }')} && \
            write_conf_value "expire_logs_days" ${MYSQL_MYSQLD_EXPIRE_LOGS_DAYS}
        MYSQL_MYSQLD_BINLOG_CHECKSUM=${MYSQL_MYSQLD_BINLOG_CHECKSUM:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "binlog-checksum" { print $2; exit }')} && \
            write_conf_value "binlog-checksum" ${MYSQL_MYSQLD_BINLOG_CHECKSUM}
        MYSQL_MYSQLD_GTID_MODE=${MYSQL_MYSQLD_GTID_MODE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "gtid-mode" { print $2; exit }')} && \
            write_conf_value "gtid-mode" ${MYSQL_MYSQLD_GTID_MODE}
        MYSQL_MYSQLD_ENFORCE_GTID_CONSISTENCY=${MYSQL_MYSQLD_ENFORCE_GTID_CONSISTENCY:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "enforce-gtid-consistency" { print $2; exit }')} && \
            write_conf_value "enforce-gtid-consistency" ${MYSQL_MYSQLD_ENFORCE_GTID_CONSISTENCY}
        MYSQL_MYSQLD_SYNC_BINLOG=${MYSQL_MYSQLD_SYNC_BINLOG:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "sync_binlog" { print $2; exit }')} && \
            write_conf_value "sync_binlog" ${MYSQL_MYSQLD_SYNC_BINLOG}
        MYSQL_MYSQLD_REPLICATE_DO_DB=${MYSQL_MYSQLD_REPLICATE_DO_DB:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "replicate-do-db" { print $2; exit }')} && \
            write_conf_value "replicate-do-db" ${MYSQL_MYSQLD_REPLICATE_DO_DB}
        MYSQL_MYSQLD_BINLOG_IGNORE_DB=${MYSQL_MYSQLD_BINLOG_IGNORE_DB:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "binlog-ignore-db" { print $2; exit }')} && \
            write_conf_value "binlog-ignore-db" ${MYSQL_MYSQLD_BINLOG_IGNORE_DB}
        MYSQL_MYSQLD_BINLOG_ROW_EVENT_MAX_SIZE=${MYSQL_MYSQLD_BINLOG_ROW_EVENT_MAX_SIZE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "binlog-row-event-max-size" { print $2; exit }')} && \
            write_conf_value "binlog-row-event-max-size" ${MYSQL_MYSQLD_BINLOG_ROW_EVENT_MAX_SIZE}
        MYSQL_MYSQLD_BINLOG_FORMAT=${MYSQL_MYSQLD_BINLOG_FORMAT:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "binlog-format" { print $2; exit }')} && \
            write_conf_value "binlog-format" ${MYSQL_MYSQLD_BINLOG_FORMAT}
        if [ ! -z ${MYSQL_MYSQLD_LARGE_PAGES} ]; then
            write_conf "large_pages"
        fi
        if [ ! -z ${MYSQL_MYSQLD_SKIP_NAME_RESOLVE} ]; then
            write_conf "skip_name_resolve"
        fi
        if [ ! -z ${MYSQL_MYSQLD_SKIP_NAME_RESOLVE} ]; then
            write_conf "skip_name_resolve"
        fi
	    if [ ! -z ${MYSQL_MYSQLD_SKIP_HOST_CACHE} ]; then
	        write_conf "skip_host_cache"
	    fi
	    if [ ! -z ${MYSQL_MYSQLD_SKIP_EXTERNAL_LOCKING} ]; then
	        write_conf "skip_external_locking"
	    fi
	    if [ ! -z ${MYSQL_MYSQLD_SKIP_INNODB_DOUBLEWRITE} ]; then
	        write_conf "skip_innodb_doublewrite"
	    fi
        MYSQL_MYSQLD_SYMBOLIC_LINKS=${MYSQL_MYSQLD_SYMBOLIC_LINKS:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "symbolic-links" { print $2; exit }')} && \
            write_conf_value "symbolic-links" ${MYSQL_MYSQLD_SYMBOLIC_LINKS}
        MYSQL_MYSQLD_EVENT_SCHEDULER=${MYSQL_MYSQLD_EVENT_SCHEDULER:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "event_scheduler" { print $2; exit }')} && \
            write_conf_value "event_scheduler" ${MYSQL_MYSQLD_EVENT_SCHEDULER}
        MYSQL_MYSQLD_DEFAULT_STORAGE_ENGINE=${MYSQL_MYSQLD_DEFAULT_STORAGE_ENGINE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "default_storage_engine" { print $2; exit }')} && \
            write_conf_value "default_storage_engine" ${MYSQL_MYSQLD_DEFAULT_STORAGE_ENGINE}
        MYSQL_MYSQLD_CHARACTER_SET_SERVER=${MYSQL_MYSQLD_CHARACTER_SET_SERVER:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "character_set_server" { print $2; exit }')} && \
            write_conf_value "character_set_server" ${MYSQL_MYSQLD_CHARACTER_SET_SERVER}
        MYSQL_MYSQLD_COLLATION_SERVER=${MYSQL_MYSQLD_COLLATION_SERVER:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "collation-server" { print $2; exit }')} && \
            write_conf_value "collation-server" ${MYSQL_MYSQLD_COLLATION_SERVER}
        MYSQL_MYSQLD_INIT_CONNECT=${MYSQL_MYSQLD_INIT_CONNECT:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "init_connect" { print $2; exit }')} && \
            write_conf_value "init_connect" ${MYSQL_MYSQLD_INIT_CONNECT}
        MYSQL_MYSQLD_CONNECT_TIMEOUT=${MYSQL_MYSQLD_CONNECT_TIMEOUT:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "connect_timeout" { print $2; exit }')} && \
            write_conf_value "connect_timeout" ${MYSQL_MYSQLD_CONNECT_TIMEOUT}
        MYSQL_MYSQLD_WAIT_TIMEOUT=${MYSQL_MYSQLD_WAIT_TIMEOUT:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "wait_timeout" { print $2; exit }')} && \
            write_conf_value "wait_timeout" ${MYSQL_MYSQLD_WAIT_TIMEOUT}
        MYSQL_MYSQLD_MAX_CONNECTIONS=${MYSQL_MYSQLD_MAX_CONNECTIONS:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "max_connections" { print $2; exit }')} && \
            write_conf_value "max_connections" ${MYSQL_MYSQLD_MAX_CONNECTIONS}
        MYSQL_MYSQLD_MAX_ALLOWED_PACKET=${MYSQL_MYSQLD_MAX_ALLOWED_PACKET:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "max_allowed_packet" { print $2; exit }')} && \
            write_conf_value "max_allowed_packet" ${MYSQL_MYSQLD_MAX_ALLOWED_PACKET}
        MYSQL_MYSQLD_MAX_CONNECT_ERRORS=${MYSQL_MYSQLD_MAX_CONNECT_ERRORS:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "max_connect_errors" { print $2; exit }')} && \
            write_conf_value "max_connect_errors" ${MYSQL_MYSQLD_MAX_CONNECT_ERRORS}
        MYSQL_MYSQLD_NET_READ_TIMEOUT=${MYSQL_MYSQLD_NET_READ_TIMEOUT:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "net_read_timeout" { print $2; exit }')} && \
            write_conf_value "net_read_timeout" ${MYSQL_MYSQLD_NET_READ_TIMEOUT}
        MYSQL_MYSQLD_NET_WRITE_TIMEOUT=${MYSQL_MYSQLD_NET_WRITE_TIMEOUT:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "net_write_timeout" { print $2; exit }')} && \
            write_conf_value "net_write_timeout" ${MYSQL_MYSQLD_NET_WRITE_TIMEOUT}
        MYSQL_MYSQLD_LOG_QUERIES_NOT_USING_INDEXES=${MYSQL_MYSQLD_LOG_QUERIES_NOT_USING_INDEXES:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "log-queries-not-using-indexes" { print $2; exit }')} && \
            write_conf_value "log-queries-not-using-indexes" ${MYSQL_MYSQLD_LOG_QUERIES_NOT_USING_INDEXES}
        MYSQL_MYSQLD_TRANSACTION_ISOLATION=${MYSQL_MYSQLD_TRANSACTION_ISOLATION:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "transaction-isolation" { print $2; exit }')} && \
            write_conf_value "transaction-isolation" ${MYSQL_MYSQLD_TRANSACTION_ISOLATION}
        MYSQL_MYSQLD_LC_MESSAGES_DIR=${MYSQL_MYSQLD_LC_MESSAGES_DIR:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "lc-messages-dir" { print $2; exit }')} && \
            write_conf_value "lc-messages-dir" ${MYSQL_MYSQLD_LC_MESSAGES_DIR}
        MYSQL_MYSQLD_INNODB_FILE_PER_TABLE=${MYSQL_MYSQLD_INNODB_FILE_PER_TABLE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_file_per_table" { print $2; exit }')} && \
            write_conf_value "innodb_file_per_table" ${MYSQL_MYSQLD_INNODB_FILE_PER_TABLE}
        MYSQL_MYSQLD_INNODB_OPEN_FILES=${MYSQL_MYSQLD_INNODB_OPEN_FILES:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_open_files" { print $2; exit }')} && \
            write_conf_value "innodb_open_files" ${MYSQL_MYSQLD_INNODB_OPEN_FILES}
        MYSQL_MYSQLD_INNODB_BUFFER_POOL_SIZE=${MYSQL_MYSQLD_INNODB_BUFFER_POOL_SIZE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_buffer_pool_size" { print $2; exit }')} && \
            write_conf_value "innodb_buffer_pool_size" ${MYSQL_MYSQLD_INNODB_BUFFER_POOL_SIZE}
        MYSQL_MYSQLD_INNODB_FLUSH_LOG_AT_TRX_COMMIT=${MYSQL_MYSQLD_INNODB_FLUSH_LOG_AT_TRX_COMMIT:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_flush_log_at_trx_commit" { print $2; exit }')} && \
            write_conf_value "innodb_flush_log_at_trx_commit" ${MYSQL_MYSQLD_INNODB_FLUSH_LOG_AT_TRX_COMMIT}
        MYSQL_MYSQLD_INNODB_FLUSH_METHOD=${MYSQL_MYSQLD_INNODB_FLUSH_METHOD:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_flush_method" { print $2; exit }')} && \
            write_conf_value "innodb_flush_method" ${MYSQL_MYSQLD_INNODB_FLUSH_METHOD}
        MYSQL_MYSQLD_INNODB_LOG_BUFFER_SIZE=${MYSQL_MYSQLD_INNODB_LOG_BUFFER_SIZE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_log_buffer_size" { print $2; exit }')} && \
            write_conf_value "innodb_log_buffer_size" ${MYSQL_MYSQLD_INNODB_LOG_BUFFER_SIZE}
        MYSQL_MYSQLD_INNODB_AUTOEXTEND_INCREMENT=${MYSQL_MYSQLD_INNODB_AUTOEXTEND_INCREMENT:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_autoextend_increment" { print $2; exit }')} && \
            write_conf_value "innodb_autoextend_increment" ${MYSQL_MYSQLD_INNODB_AUTOEXTEND_INCREMENT}
        MYSQL_MYSQLD_INNODB_CONCURRENCY_TICKETS=${MYSQL_MYSQLD_INNODB_CONCURRENCY_TICKETS:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_concurrency_tickets" { print $2; exit }')} && \
            write_conf_value "innodb_concurrency_tickets" ${MYSQL_MYSQLD_INNODB_CONCURRENCY_TICKETS}
        MYSQL_MYSQLD_INNODB_DATA_FILE_PATH=${MYSQL_MYSQLD_INNODB_DATA_FILE_PATH:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_data_file_path" { print $2; exit }')} && \
            write_conf_value "innodb_data_file_path" ${MYSQL_MYSQLD_INNODB_DATA_FILE_PATH}
        MYSQL_MYSQLD_INNODB_LOG_FILES_IN_GROUP=${MYSQL_MYSQLD_INNODB_LOG_FILES_IN_GROUP:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_log_files_in_group" { print $2; exit }')} && \
            write_conf_value "innodb_log_files_in_group" ${MYSQL_MYSQLD_INNODB_LOG_FILES_IN_GROUP}
        MYSQL_MYSQLD_INNODB_OLD_BLOCKS_TIME=${MYSQL_MYSQLD_INNODB_OLD_BLOCKS_TIME:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_old_blocks_time" { print $2; exit }')} && \
            write_conf_value "innodb_old_blocks_time" ${MYSQL_MYSQLD_INNODB_OLD_BLOCKS_TIME}
        MYSQL_MYSQLD_INNODB_STATS_ON_METADATA=${MYSQL_MYSQLD_INNODB_STATS_ON_METADATA:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_stats_on_metadata" { print $2; exit }')} && \
            write_conf_value "innodb_stats_on_metadata" ${MYSQL_MYSQLD_INNODB_STATS_ON_METADATA}
        MYSQL_MYSQLD_INNODB_FAST_SHUTDOWN=${MYSQL_MYSQLD_INNODB_FAST_SHUTDOWN:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_fast_shutdown" { print $2; exit }')} && \
            write_conf_value "innodb_fast_shutdown" ${MYSQL_MYSQLD_INNODB_FAST_SHUTDOWN}
        MYSQL_MYSQLD_INNODB_LOG_FILE_SIZE=${MYSQL_MYSQLD_INNODB_LOG_FILE_SIZE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "innodb_log_file_size" { print $2; exit }')} && \
            write_conf_value "innodb_log_file_size" ${MYSQL_MYSQLD_INNODB_LOG_FILE_SIZE}
        MYSQL_MYSQLD_PERFORMANCE_SCHEMA=${MYSQL_MYSQLD_PERFORMANCE_SCHEMA:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "performance_schema" { print $2; exit }')} && \
            write_conf_value "performance_schema" ${MYSQL_MYSQLD_PERFORMANCE_SCHEMA}
        MYSQL_MYSQLD_EXPLICIT_DEFAULTS_FOR_TIMESTAMP=${MYSQL_MYSQLD_EXPLICIT_DEFAULTS_FOR_TIMESTAMP:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "explicit_defaults_for_timestamp" { print $2; exit }')} && \
            write_conf_value "explicit_defaults_for_timestamp" ${MYSQL_MYSQLD_EXPLICIT_DEFAULTS_FOR_TIMESTAMP}
        MYSQL_MYSQLD_QUERY_CACHE_SIZE=${MYSQL_MYSQLD_QUERY_CACHE_SIZE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "query_cache_size" { print $2; exit }')} && \
            write_conf_value "query_cache_size" ${MYSQL_MYSQLD_QUERY_CACHE_SIZE}
        MYSQL_MYSQLD_QUERY_CACHE_TYPE=${MYSQL_MYSQLD_QUERY_CACHE_TYPE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "query_cache_type" { print $2; exit }')} && \
            write_conf_value "query_cache_type" ${MYSQL_MYSQLD_QUERY_CACHE_TYPE}
        MYSQL_MYSQLD_QUERY_CACHE_MIN_RES_UNIT=${MYSQL_MYSQLD_QUERY_CACHE_MIN_RES_UNIT:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "query_cache_min_res_unit" { print $2; exit }')} && \
            write_conf_value "query_cache_min_res_unit" ${MYSQL_MYSQLD_QUERY_CACHE_MIN_RES_UNIT}
        MYSQL_MYSQLD_JOIN_BUFFER_SIZE=${MYSQL_MYSQLD_JOIN_BUFFER_SIZE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "join_buffer_size" { print $2; exit }')} && \
            write_conf_value "join_buffer_size" ${MYSQL_MYSQLD_JOIN_BUFFER_SIZE}
        MYSQL_MYSQLD_READ_RND_BUFFER_SIZE=${MYSQL_MYSQLD_READ_RND_BUFFER_SIZE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "read_rnd_buffer_size" { print $2; exit }')} && \
            write_conf_value "read_rnd_buffer_size" ${MYSQL_MYSQLD_READ_RND_BUFFER_SIZE}
        MYSQL_MYSQLD_TABLE_DEFINITION_CACHE=${MYSQL_MYSQLD_TABLE_DEFINITION_CACHE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "table_definition_cache" { print $2; exit }')} && \
            write_conf_value "table_definition_cache" ${MYSQL_MYSQLD_TABLE_DEFINITION_CACHE}
        MYSQL_MYSQLD_TABLE_OPEN_CACHE=${MYSQL_MYSQLD_TABLE_OPEN_CACHE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "table_open_cache" { print $2; exit }')} && \
            write_conf_value "table_open_cache" ${MYSQL_MYSQLD_TABLE_OPEN_CACHE}
        MYSQL_MYSQLD_THREAD_CACHE_SIZE=${MYSQL_MYSQLD_THREAD_CACHE_SIZE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "thread_cache_size" { print $2; exit }')} && \
            write_conf_value "thread_cache_size" ${MYSQL_MYSQLD_THREAD_CACHE_SIZE}
        MYSQL_MYSQLD_TMP_TABLE_SIZE=${MYSQL_MYSQLD_TMP_TABLE_SIZE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "tmp_table_size" { print $2; exit }')} && \
            write_conf_value "tmp_table_size" ${MYSQL_MYSQLD_TMP_TABLE_SIZE}
        MYSQL_MYSQLD_MAX_HEAP_TABLE_SIZE=${MYSQL_MYSQLD_MAX_HEAP_TABLE_SIZE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "max_heap_table_size" { print $2; exit }')} && \
            write_conf_value "max_heap_table_size" ${MYSQL_MYSQLD_MAX_HEAP_TABLE_SIZE}
        MYSQL_MYSQLD_THREAD_HANDLING=${MYSQL_MYSQLD_THREAD_HANDLING:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "thread_handling" { print $2; exit }')} && \
            write_conf_value "thread_handling" ${MYSQL_MYSQLD_THREAD_HANDLING}
        MYSQL_MYSQLD_THREAD_POOL_SIZE=${MYSQL_MYSQLD_THREAD_POOL_SIZE:-$("$@" --verbose --help 2>/dev/null | awk '$1 == "thread_pool_size" { print $2; exit }')} && \
            write_conf_value "thread_pool_size" ${MYSQL_MYSQLD_THREAD_POOL_SIZE}
    fi

    # New install?
    if [ ! -d "$DATADIR/mysql" ]; then
	    echo "Initializing database ..."
	    if [ -z "$MYSQL_ROOT_PASSWORD" -a -z "$MYSQL_ALLOW_EMPTY_PASSWORD" -a -z "$MYSQL_RANDOM_ROOT_PASSWORD" ]; then
	        echo >&2 "error: database is uninitialized and password option is not specified "
	        echo >&2 "  You need to specify one of MYSQL_ROOT_PASSWORD, MYSQL_ALLOW_EMPTY_PASSWORD and MYSQL_RANDOM_ROOT_PASSWORD"
	        exit 1
	    fi

	    # Create base directory structure from any *.tar.gz
	    for f in /docker-entrypoint-initdb.d/*; do
	        case "$f" in
		        *.tar.gz) echo "$0: extracting $f to /"; tar -xf "$f" -C /; echo ;;
	        esac
	    done

	    "$@" --initialize-insecure
	    echo "Initialization successfully completed."

	    "$@" --skip-networking &
	    pid="$!"

	    mysql=( mysql --protocol=socket -uroot )

	    for i in 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00; do
	        if echo "SELECT 1" | "${mysql[@]}" &> /dev/null; then
		        break
	        fi
	        echo "Waiting for starting MySQL in restricted mode ..."
	        sleep 1
	    done
	    if [ "$i" = 0 ]; then
	        echo >&2 "Waiting for starting MySQL in restricted mode failed."
	        exit 2
	    fi

	    echo "MySQL started in restricted mode."
	    if [ -n "${REPLICATION_SLAVE}" ]; then
	        echo " - Configuring MySQL replication as slave ..."
	        if [ -z "${MYSQL_MASTER_ADDR}" ]; then
		        MYSQL_MASTER_ADDR=mysql
	        fi

	        if [ -z "${MYSQL_MASTER_PORT}" ]; then
		        MYSQL_MASTER_PORT=3306
	        fi

	        if [ -n "${MYSQL_MASTER_ADDR}" ] && [ -n "${MYSQL_MASTER_PORT}" ]; then
		        if [ ! -f /tmp/.replication ]; then
		            echo " - Setting master connection info on slave"
		            "${mysql[@]}" <<-EOSQL
			            SET @@SESSION.SQL_LOG_BIN=0;
			            CHANGE MASTER TO MASTER_HOST='${MYSQL_MASTER_ADDR}', MASTER_AUTO_POSITION=1, MASTER_USER='${MYSQL_REPLICATION_USER}', MASTER_PASSWORD='${MYSQL_REPLICATION_PASS}', MASTER_PORT=${MYSQL_MASTER_PORT}, MASTER_CONNECT_RETRY=30;
			            START SLAVE;
EOSQL
		            touch /tmp/.replication
		        else
		            echo " - MySQL replication slave already configured, skip"
		        fi
	        else
		        echo " - Cannot configure slave, please link it to another MySQL container with alias as 'mysql'"
		        exit 3
	        fi
	    fi

	    if [ -z "$MYSQL_INITDB_SKIP_TZINFO" ]; then
	        # sed is for https://bugs.mysql.com/bug.php?id=20545
	        mysql_tzinfo_to_sql /usr/share/zoneinfo | sed "s/Local time zone must be set--see zic manual page/FCTY/" | "${mysql[@]}" mysql
	    fi

	    if [ ! -z "$MYSQL_RANDOM_ROOT_PASSWORD" ]; then
	        MYSQL_ROOT_PASSWORD="$(pwgen -1 32)"
	        echo "Reset new root password to $MYSQL_ROOT_PASSWORD"
	    fi

	    "${mysql[@]}" <<-EOSQL
	        SET @@SESSION.SQL_LOG_BIN=0;
	        DELETE FROM mysql.user ;
	        CREATE USER 'root'@'%' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}' ;
	        GRANT ALL ON *.* TO 'root'@'%' WITH GRANT OPTION;
	        DROP DATABASE IF EXISTS test;
	        FLUSH PRIVILEGES;
EOSQL

	    if [ ! -z "$MYSQL_ROOT_PASSWORD" ]; then
	        mysql+=( -p"${MYSQL_ROOT_PASSWORD}" )
	    fi

	    # Set MySQL REPLICATION - MASTER
	    if [ -n "${REPLICATION_MASTER}" ]; then
	        echo " - Configuring MySQL replication as master ..."
	        if [ ! -f /tmp/.replication ]; then
		        echo "   - Creating a log user ${REPLICATION_USER}:${REPLICATION_PASS}"

		        "${mysql[@]}" <<-EOSQL
		            SET @@SESSION.SQL_LOG_BIN=0;
		            CREATE USER '${MYSQL_REPLICATION_USER}'@'%' IDENTIFIED BY '${MYSQL_REPLICATION_PASS}';
		            GRANT REPLICATION SLAVE ON *.* TO '${MYSQL_REPLICATION_USER}'@'%';
		            FLUSH PRIVILEGES;
		            RESET MASTER;
EOSQL

		        touch /tmp/.replication
	        else
		        echo " - MySQL replication master already configured, skip"
	        fi

	        if [ "$MYSQL_DATABASE" ]; then
		        echo "CREATE DATABASE IF NOT EXISTS \`$MYSQL_DATABASE\`;" | "${mysql[@]}"
		        mysql+=( "$MYSQL_DATABASE" )
	        fi

	        if [ "$MYSQL_USER" -a "$MYSQL_PASSWORD" ]; then
		        echo "CREATE USER '$MYSQL_USER'@'%' IDENTIFIED BY '$MYSQL_PASSWORD';" | "${mysql[@]}"

		        if [ "$MYSQL_DATABASE" ]; then
		            echo "GRANT ALL ON \`$MYSQL_DATABASE\`.* TO '$MYSQL_USER'@'%';" | "${mysql[@]}"
		        fi
		        echo "FLUSH PRIVILEGES;" | "${mysql[@]}"
	        fi
	    fi

	    echo
	    for f in /docker-entrypoint-initdb.d/*; do
	        case "$f" in
		        *.sh)     echo "$0: running $f"; . "$f" ;;
		        *.sql)    echo "$0: running $f"; "${mysql[@]}" < "$f"; echo ;;
		        *.sql.gz) echo "$0: running $f"; gunzip -c "$f" | "${mysql[@]}"; echo ;;
		        *) ;;
	        esac
	        echo
	    done

	    if [ ! -z "$MYSQL_ONETIME_PASSWORD" ]; then
	        "${mysql[@]}" <<-EOSQL
		    ALTER USER 'root'@'%' PASSWORD EXPIRE;
EOSQL
	    fi

	    if ! kill -s TERM "$pid" || ! wait; then
            echo >&2 " - MySQL restricted mode process failed."
	        exit 4
	    fi

        echo
        echo "Ready for start up in production mode."
        echo
    fi
    chown -R mysql:mysql "$DATADIR"
fi

exec "$@"
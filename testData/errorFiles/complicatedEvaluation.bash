#!/bin/sh

case ${severity} in
      debug|7) severityPrefix="${__LogPrefixSeverity7:->>> [____DEBUG]}" ;;
esac

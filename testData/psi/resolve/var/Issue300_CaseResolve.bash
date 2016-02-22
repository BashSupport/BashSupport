#!/usr/bin/env bash

NULL_SHA1='123'
case "123,123" in
    *,${<ref>NULL_SHA1})
        echo 'foo';;
esac
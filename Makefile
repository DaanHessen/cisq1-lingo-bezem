.PHONY: test test-di

test:
	mvn verify

test-ci:
	mvn verify "-Dspring.profiles.active=ci"
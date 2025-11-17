.PHONY: test test-ci coverage clean-db

test:
	mvn verify

test-ci:
	mvn verify "-Dspring.profiles.active=ci"

coverage:
	mvn clean test jacoco:report
	@echo "Report generated at: target/site/jacoco/index.html"

clean-db:
	@echo "Cleaning database..."
	PGPASSWORD=cisq1-lingo psql -h localhost -p 15432 -U cisq1-lingo -d cisq1-lingo -c "TRUNCATE TABLE games_past_rounds, rounds_history, feedback_marks, feedback, rounds, games CASCADE;"
	@echo "Database cleaned successfully"

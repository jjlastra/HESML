# Executing Reprozip for packing the experiment

	sudo rm -r .reprozip-trace
	sudo rm umlsBenchmark.rpz

    	sudo reprozip trace ./execute_experiments_for_reprozip.sh

	sudo reprozip pack umlsBenchmark.rpz

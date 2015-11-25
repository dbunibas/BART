BART
====

BART (Benchmarking Algorithms for data Repairing and Translation) is an error-generation tool for data cleaning applications. Its purpose is to introduce errors into clean databases for the purpose of benchmarking data-repairing algorithms. It provides users with the highest possible level of control over the error-generation process, and at the same time scales nicely to large databases. This is far from trivial, since, as we show in our technical papers, the error-generation problem is surprisingly challenging, and in fact, NP-complete. To scale to millions of tuples, the system relies on several non-trivial optimizations, including a new symmetry property of data quality constraints.

Additional material about the project can be found at the following address: http://db.unibas.it/projects/bart/

### How to import project in NetBeans ####
1. In NetBeans, File -> Open projects... and select the project folder
2. Execute ant target task `gfp`, either using command-line `ant gfp`, or using NetBeans (in the projects windows, right click on build.xml -> Run Target -> Other Targets -> gfp)

---

### How to configure an EGTask
An EGTask is specified in an .xml file, with 3 main sections:

#####**1. Database configuration:** #####
Is used to specify the JDBC parameters to access the DBMS.
[PostgreSQL](http://www.postgresql.org/) and [H2](http://www.h2database.com) DBMS are supported.
    Data can be automatically loaded into the database from XML files.

#####**2. Dependencies specification:** #####

#####**3. Task configuration:** #####
* **printLog**: (default = false)
* **recreateDBOnStart**: (default = false) To load DB every time on start
* **applyCellChanges**: (default = false) To apply cell changes
* **cloneTargetSchema**: (default = true) To apply cell changes on a copy of the original target
* **useDeltaDBForChanges**: (default = true) To use an optimized strategy for updates
* **checkChanges**: (default = false) To check, at the end of the process, if changes are detectable
* **generateAllChanges**: To generate all possible changes (default = false) - slow, only for toy examples
* **avoidInteractions**: Avoid interactions among changes. (default = true)
* **errorPercentages**: Error percentages for dependencies and comparisons. All percentages are wrt table sizes (# tuples)


---

### How to run an EGTask
Execute script `./run <egtask.xml>`, for example `./run.sh misc/resources/employees/employees-dbms-2k-egtask.xml`

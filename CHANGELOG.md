# 1.1.15

* Added MatterOpedia, a system to search what items have a specific matter value
* Added a tag to skip calculation checks `replication:skip_calculation`
*

# 1.1.14

* Updated titanium dep

# 1.1.13

* Fixed recipe calculation checking tag values too early, closes #26

# 1.1.12

* Added GuideMe support

# 1.1.11

* Added `/replication export-to-csv` to export matter values to a csv file, closes #24

# 1.1.10

* Changed when Replication Calculation is executed to avoid multiple runs, closes #25

# 1.1.9

* Matter Tanks can be locked now, and fixed them having their nbt reapplied constantly
* Replicators will now autobalance their crafting jobs, closes #23
* Added mission tags, closes #22

# 1.1.8

* Fixed Disintegrator being able to work even if the internal buffer was full
* Fixed Disintegrator animation in worlds with long game time

# 1.1.7

* Changed Replicators to consume power only when working for real

# 1.1.6

* Fixed Replicators going to infinity when their inventory is full

# 1.1.5

* Fixed matter components being added to empty stacks and Matter components are now always shown when the Replication
  Terminal is open

# 1.1.4

* Added regions to JEI so items don't overlap replication screens, closes #17
* Added a tint on the background of the matter tank numbers in the terminal to improve readability
* Removed the filter that stopped the items with extra components from being calculated

# 1.1.3
* Fixed server side crashes

# 1.1.2
* Blacklisted replication from Dark Mode Everywhere

# 1.1.1
* Reduced Replica Ore Spawn rate
* Fixed culling on Replica Ore

# 1.1.0
* Ported to 1.21, recipe calculation much improved

# 1.0.4
* Changed the pipe recipe to give you 8 items
* Fixed some tank transfer issues in the Disintegrator

# 1.0.3
* Slowed Replica animations
* Fixed tags not working, closes #13 (Check Readme.md)
* Added support for blacklisting items from being disintegrated, closes #14 (Check Readme.md)

# 1.0.2
* Added a toast notification to show when matter data has been calculated

# 1.0.1
* Fixed server side issues

# 1.0.0

* Initial release
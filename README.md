Web aggregator framework
========================

A Java framework that makes creating web aggregators like googlenews, fast and easy.

----

This framework is a result of my work on a scholarship aggregator that I made as a 3rd year student at Faculty of Computer Science and Engineering at Ss Cyril and Methodius University, Skopje, Macedonia. Unfortunately, the scholarship thing never took off, but the framework that was developed as part of the project is general and can be applied to other problems as well.


If you are interested in using this framework you should first look at the manual (if you want to jump to coding asap at least skim the first section that describes the general concepts used in the framework). You can also use the code from the scholarship aggregator (sorry, no front-end code) as reference and a good starting point for your project.


---

Key Features:

 - built-in support for mongoDB a JSON based schema less database, ideal for storing diverse data, yet fully customizable (uses Jakson for de/serialization)
 - fully integrated with Weka the open source classification library
 - a ton of helper function that accelerate your coding
    - text-feature extraction
    - automatic data labeling (Weka)
    - near-duplicate identification (Weka)
    - querying on wide array of search criteria
 - well-documented and flexible external API 

---

Requirements:

Jackson 1.7
Weka 3.6
MongoDB java driver 

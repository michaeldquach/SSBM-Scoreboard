# SSBM-Scoreboard

SSBM Scoreboard is a program that writes text and images to update OBS overlays for tournament organizers. 
It also interacts with Challonge's API, to allow for pulling of tournament and match information, automatically updating the scoreboard. 
TO's can also push a completed match's result from the scoreboard directly to Challonge, updating the bracket.
The program can be used solely to update OBS overlays, if TO's do not wish to interact with Challonge.

## Usage

* Obtain a Challonge API key from https://challonge.com/settings/developer.

* Click "Login to Challonge".

* Select a tournament linked to your Challonge account. (Note: tournament must be marked as "Started" on Challonge.) Click "Load Tournament".

* Select a bracket match.

* Update scores, and any incomplete fields (characters, commentators). 

* Click "Save and Output" to update the OBS overlay.

* Click "Upload Match" to upload the completed match to Challonge.

## OBS Overlay

Text files and images are located in the folder "OBS Output". To link them to OBS, add them as a text or image source to your scene. Ensure "Read from file" is ticked.

## Extras

* A sample OBS scene with text files and image files linked to the overlay has been included.

#### Use this at your own risk.

* Replacing the existing obs-text.dll at 
>C:\Program Files\obs-studio\obs-plugins\64bit 

with the included file allows OBS to poll text files every 20 ms rather than the default 1000 ms, allowing the overlay to refresh     faster when output files are updated. 

See here: https://obsproject.com/forum/threads/read-from-text-file-refresh-rate.73589/post-460054

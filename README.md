# ChunkGenRateLimiter2
 
When players reach unexplored land in Minecraft, the new areas must be procedurally generated, which is very CPU expensive and problematic on multiplayer servers with many players since Minecraft is single-threaded. Many servers solve this problem by pre-generating all of the land within a border. However, to give players an unrestrictive amount of land to play in, pre-generating chunks usually wastes hundreds of gigabytes of disk space on land that will never be reached by a single player. This can obviously cause problems when hosting a server on a machine with insufficient disk space.

This plugin is a compromise solution that imposes a soft limit on the number of chunks each player can generate per minute. Once the limit is reached, while the player continues to explore land they will be restricted from using the fastest methods of movement (elytras, horses, boats, and ender pearls) and their flying and walking speed will be slightly reduced. After they stop generating new land, they will be able to move regularly. This way, their movement is not restricted within land that has already been generated.

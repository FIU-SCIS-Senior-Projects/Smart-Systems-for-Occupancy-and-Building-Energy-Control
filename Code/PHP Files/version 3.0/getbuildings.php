<?php


// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$sql = "SELECT name, shortname, latitude, longitude, shape, width, height, floorplanid, floorid, venueid, floor FROM  `FloorPlans` INNER JOIN  `Buildings` ON FloorPlans.building_floorplan = Buildings.name";

$result = mysqli_query($conn, $sql);


if (mysqli_num_rows($result) > 0) {
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
        echo "name:" . $row["name"]. ":shortname:" . $row["shortname"]. ":latitude:" . $row["latitude"]. ":longitude:" .$row["longitude"]. ":shape:" .$row["shape"]. ":width:" .$row["width"]. ":height:" .$row["height"]. ":floorplanid:" .$row["floorplanid"]. ":floorid:" .$row["floorid"]. ":venueid:" .$row["venueid"]. ":floor:" .$row["floor"]. "+";
    }
} else {
    echo "Not A User";
}
mysqli_close($conn);
?>

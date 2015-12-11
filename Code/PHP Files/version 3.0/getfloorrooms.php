<?php


// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
$floorplan = $_POST['floorplan'];
$floor = $_POST['floor'];

$sql = "select * from FloorPlanRooms where floor = '".$floor."' AND floor_plan = '".$floorplan."'";
$result = mysqli_query($conn, $sql);


if (mysqli_num_rows($result) > 0) {
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
        echo "roomNumber:" . $row["room_number"]. ":x:" . $row["lat"]. ":y:" . $row["lng"]. ":shape:" .$row["shape"]. ":width:" .$row["width"]. ":height:" .$row["height"]. "+";
    }
} else {
    echo "No rooms found";
}
mysqli_close($conn);
?>
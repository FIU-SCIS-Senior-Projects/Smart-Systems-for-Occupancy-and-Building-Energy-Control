<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';
// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$roomNum = $_POST['room_number'];
$floorplan = $_POST['floorplan'];
$floor = $_POST['floor'];
$x = $_POST['x'];
$y = $_POST['y'];
$shape = $_POST['shape'];
$width = $_POST['width'];
$height = $_POST['height'];

$sql = "SELECT * FROM FloorPlanRooms WHERE room_number = '" . $roomNum . "' AND floor_plan = '" . $floorplan ."';";

$result = mysqli_query($conn, $sql);

$user_id = null;

if($result) {
    //echo "Succesful 1";
    if(mysqli_num_rows($result) > 0){ 

        $sql = "UPDATE FloorPlanRooms SET room_number = " . $roomNum. ", floor_plan = '" . $floorplan. "', floor = '" . $floor . "', lat = " . $x .", lng = " . $y . ", shape = '" . $shape . "', width = " . $width . ", height = " . $height . " WHERE room_number = " . $roomNum. " AND floor_plan = '" . $floorplan ."';";
        echo "updating";
        echo $sql;
        $result = mysqli_query($conn, $sql);
        
        if($result) {
            echo 'Successful';
        }
        else {
            echo 'Mysql Error 2: '. mysqli_error($conn);
        }
    }
    else
    {
        $sql = "INSERT INTO FloorPlanRooms (room_number, floor_plan, floor, lat, lng, shape, width, height) VALUES (".$roomNum.", '".$floorplan."','".$floor."', ".$x.", ".$y.", '".$shape."', ".$width.", ".$height.");";
        echo "Adding new row";
        echo $sql;
        
        $result = mysqli_query($conn,$sql);
        if($result) {
            echo 'Successful';
        }
        else {
            echo 'Mysql Error 3: '. mysqli_error($conn);
        }
        echo $sql;
    }
}
else {
    echo 'Mysql Error 1: '. mysqli_error($conn);
}

mysqli_close($conn);
?>

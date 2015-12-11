<?php


// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$userId = $_POST['user_id'];

$sql = "SELECT * FROM user_rewards WHERE user_id ='".$userId."'";
$result = mysqli_query($conn, $sql);

$list = array();

if (mysqli_num_rows($result) > 0) {
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
        
	$list[] = array('time_stamp' => $row["time_stamp"], 'description' => $row["description"], 'points' => $row["points"]);
    }

	echo json_encode($list, JSON_FORCE_OBJECT);

} else {
    echo 'MySQL Error: '. mysqli_error($conn);
}
mysqli_close($conn);
?>

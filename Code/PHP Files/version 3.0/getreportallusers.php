<?php


// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}


//get all users data
$sql = "SELECT name, rewards_total FROM user ORDER BY rewards_total DESC";

$result = mysqli_query($conn, $sql);

$list = array();


if (mysqli_num_rows($result) > 0) {
    // output data of each row
    
    while($row = mysqli_fetch_assoc($result)) {
        $counter ++;
        //echo "=== " . $row["region_name"] . " [[[[[ " . $row["plugload"] . "\n";
	$list[] = array('name' => $row["name"], 'description' => "user", 'value' => $row["rewards_total"]);
    }

	echo json_encode($list, JSON_FORCE_OBJECT);

}

else {
    echo 'MySQL Error: '. mysqli_error($conn);
}
mysqli_close($conn);
?>

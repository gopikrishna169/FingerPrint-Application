import React, { useState, useEffect } from 'react';
import { View, Text, Button, Alert, TextInput, StyleSheet } from 'react-native';
import ReactNativeBiometrics from 'react-native-biometrics';
import { NativeModules } from 'react-native';
const { FingerprintScannerModule } = NativeModules;

const registerFingerprint = async (phoneNumber) => {
  FingerprintScannerModule.startFingerprintScan(
    (message) => {
        console.log('Fingerprint captured successfully:', message);
        // Call Registration API
        handleSubmit(phoneNumber, message)
        Alert.alert('Success', 'User ID: ' + message)
    },
    (error) => {
        console.error('Fingerprint scan error:', error);
    }
  );
};

const handleSubmit = async (mobile, textEncodedFIR) => {
  const payload = { mobile, textEncodedFIR };

  try {
    const response = await fetch('http://ustech-001-site22.mtempurl.com/api/Fingerprint/EnrollFingerprint', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });

    const responseData = await response.json();

    if (response.ok) {
      Alert.alert('Success', 'Data posted successfully');
    } else {
      throw new Error(responseData.message || 'Something went wrong');
    }
  } catch (error) {
    console.error('Error posting data:', error);
    Alert.alert('Error', 'Failed to post data');
  }
};

const Registration = () => {
  const [phoneNumber, setPhoneNumber] = useState(0);

  const handleRegister = () => {
    if (phoneNumber != null && phoneNumber != 0) {
      registerFingerprint(phoneNumber);
    } else {
      Alert.alert('Error', 'Please enter your phoneNumber');
    }
  };

  return (
    <View style={styles.container}>
      <Text >Register User</Text>
      <TextInput
        placeholder="Enter your Phone Number"
        value={phoneNumber}
        onChangeText={setPhoneNumber}
      />
      <View style={styles.space} />
      <Button title="Register Fingerprint" onPress={handleRegister} style={styles.button} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    marginTop: 20
  },
  title: {
    fontSize: 20,
    marginBottom: 30,
  },
  button: {
    marginTop: 30,
  },
  space: {
    height: 20,
  }
});
export default Registration;

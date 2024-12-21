import React, { useEffect, useState } from 'react';
import { View, Text, Button } from 'react-native';
import FingerprintScanner from 'react-native-fingerprint-scanner';

const FingerprintAuth = () => {
  const [fingerprintSupported, setFingerprintSupported] = useState(false);
  const [authenticated, setAuthenticated] = useState(false);

  useEffect(() => {
    // Check if the device supports fingerprint authentication
    FingerprintScanner.isSensorAvailable()
      .then((result) => {
        console.log('Fingerprint scanner available');
        setFingerprintSupported(true);
      })
      .catch((error) => {
        console.log('Fingerprint scanner not available');
        setFingerprintSupported(false);
      });

    return () => {
      FingerprintScanner.release();  // Cleanup on unmount
    };
  }, []);

  const handleAuthenticate = () => {
    FingerprintScanner.authenticate({ description: 'Scan your fingerprint' })
      .then(() => {
        setAuthenticated(true);
        console.log('Fingerprint authentication successful');
      })
      .catch((error) => {
        setAuthenticated(false);
        console.log('Fingerprint authentication failed', error);
      });
  };

  return (
    <View>
      <Text>Fingerprint Authentication</Text>
      {fingerprintSupported ? (
        <>
          <Button title="Authenticate" onPress={handleAuthenticate} />
          {authenticated && <Text>Authentication Successful!</Text>}
        </>
      ) : (
        <Text>Fingerprint scanner not available on this device.</Text>
      )}
    </View>
  );
};

export default FingerprintAuth;
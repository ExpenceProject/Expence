import { Box, Button, Flex, Link, Span } from '@chakra-ui/react';

import { ColorModeButton } from '../ui/color-mode';

const Header = () => {
  return (
    <Box
      as="header"
      bg="surface"
      display="flex"
      flexDirection="row"
      justifyContent="space-between"
      p={4}
      w="100%"
    >
      <Flex>
        <Flex align="center" justify="center">
          <Span fontSize="xl" color="textRaw">
            Expence
          </Span>
        </Flex>
      </Flex>
      <Flex align="center" justify="center" gap="6">
        <Flex align="center" justify="center" gap="10">
          <Link
            fontSize="lg"
            color="text"
            textDecor="none"
            href="/groups"
            _hover={{ color: 'textHover' }}
            _active={{ color: 'textHover' }}
          >
            Groups
          </Link>
          <Link
            fontSize="lg"
            color="text"
            textDecor="none"
            href="/tracker"
            _hover={{ color: 'textHover' }}
            _active={{ color: 'textHover' }}
          >
            Tracker
          </Link>
          <Link
            fontSize="lg"
            color="text"
            textDecor="none"
            href="/about"
            _hover={{ color: 'textHover' }}
            _active={{ color: 'textHover' }}
          >
            About
          </Link>
        </Flex>
        <ColorModeButton />
        <Button
          fontSize="lg"
          bg="primary"
          color="textBg"
          _hover={{ bg: 'hover' }}
        >
          Sign Up
        </Button>
      </Flex>
    </Box>
  );
};

export default Header;

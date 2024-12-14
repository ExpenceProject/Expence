import Drawer from '@/components/custom/drawer/drawer';
import UserMenu from '@/components/custom/user-menu/user-menu';
import { LogoIcon } from '@/components/icons/logo';
import { ColorModeButton } from '@/components/ui/color-mode';
import {
  coreMobilePaddingX,
  corePaddingX,
  maxWebsiteWidth,
} from '@/style/variables';
import { openLoginModalAtom } from '@/utils/atoms/modal-atoms';
import { useUser } from '@/utils/providers/user-provider/use-user';
import { Box, Button, Flex, Span, useBreakpointValue } from '@chakra-ui/react';
import { useAtom } from 'jotai';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

import HeaderNav from './header-nav';

const StyledLogoLink = styled(Link)`
  color: var(--ck-colors-text-raw);
  text-decoration: none;
  outline: none;
`;

const Header = () => {
  const currentBreakpoint = useBreakpointValue({
    sm: 'sm',
    md: 'md',
    lg: 'lg',
    xl: 'xl',
  });

  const [, openLoginModal] = useAtom(openLoginModalAtom);

  const { user } = useUser();

  return (
    <Box
      as="header"
      bg="background"
      display="flex"
      justifyContent="center"
      alignItems="center"
      px={{ base: coreMobilePaddingX, md: corePaddingX }}
      py={4}
      w="100%"
      borderBottom="1px solid rgba(161, 161, 161, 0.3)"
    >
      <Flex
        w="100%"
        maxW={maxWebsiteWidth}
        justify="space-between"
        align="center"
      >
        <StyledLogoLink to="/">
          <Flex align="center" justify="center" gap="1">
            <LogoIcon fill="primary" width={35} />
            <Span fontSize="2xl" color="textRaw" fontWeight="bold">
              Expence
            </Span>
          </Flex>
        </StyledLogoLink>
        <Flex align="center" justify="center" gap={{ base: 2, md: 6 }}>
          {currentBreakpoint === 'sm' || !currentBreakpoint ? (
            <Drawer />
          ) : (
            <HeaderNav />
          )}
          <ColorModeButton />
          {user ? (
            <UserMenu />
          ) : (
            <Button
              fontSize="lg"
              bg="primary"
              color="textBg"
              _hover={{ bg: 'hover' }}
              onClick={openLoginModal}
            >
              Sign In
            </Button>
          )}
        </Flex>
      </Flex>
    </Box>
  );
};

export default Header;

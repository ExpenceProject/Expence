import { Flex } from '@chakra-ui/react';
import { FC } from 'react';

import HeaderLink from './header-link';

type HeaderNavProps = {
  direction?: 'row' | 'column';
};

const HeaderNav: FC<HeaderNavProps> = ({ direction = 'row' }) => {
  return (
    <Flex flexDirection={direction} align="center" justify="center" gap="8">
      <HeaderLink text="Groups" href="/groups" />
      <HeaderLink text="Tracker" href="/tracker" />
      <HeaderLink text="About" href="/about" />
    </Flex>
  );
};

export default HeaderNav;

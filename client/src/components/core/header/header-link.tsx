import { Link } from '@chakra-ui/react';
import { FC } from 'react';

type HeaderLinkProps = {
  text: string;
  href: string;
};

const HeaderLink: FC<HeaderLinkProps> = ({ text, href }) => {
  return (
    <Link
      fontSize="lg"
      color="text"
      textDecor="none"
      href={href}
      _hover={{ color: 'textHover' }}
      _active={{ color: 'textHover' }}
      _focus={{ outline: 'none' }}
    >
      {text}
    </Link>
  );
};

export default HeaderLink;

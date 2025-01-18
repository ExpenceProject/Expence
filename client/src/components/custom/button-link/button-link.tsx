import { Button } from '@chakra-ui/react';
import { FC } from 'react';
import { HiMiniArrowRight } from 'react-icons/hi2';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

type ButtonLinkProps = {
  text: string;
  href: string;
  backgroundColor?: string;
  color?: string;
  hoverColor?: string;
  fontSize?: string;
  px?: number;
  py?: number;
  mt?: number;
  mb?: number;
};

const StyledButtonLink = styled(Link)`
  color: var(--ck-colors-text-bg);
  text-decoration: none;
  width: 100%;
  height: 100%;
  padding: 0;
  display: flex;
  gap: 5px;
  align-items: center;
  justify-content: center;
  outline: none;
  transition: all 0.15s ease;
`;

const ButtonLink: FC<ButtonLinkProps> = ({
  text,
  href,
  hoverColor = 'hoverPrimary',
  px = 20,
  py = 20,
  mt = 4,
  mb = 4,
}) => {
  return (
    <Button
      size={{ base: 'xs', sm: 'sm', md: 'xl' }}
      fontSize={{ base: 'sm', sm: 'md', md: 'lg' }}
      bg="primary"
      mt={mt}
      w="min-content"
      mb={mb}
      _hover={{ bg: hoverColor }}
      p={0}
    >
      <StyledButtonLink
        to={href}
        style={{
          padding: `${py}px ${px}px`,
        }}
      >
        {text} <HiMiniArrowRight />
      </StyledButtonLink>
    </Button>
  );
};

export default ButtonLink;
